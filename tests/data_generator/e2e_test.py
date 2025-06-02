import unittest
import logging
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
import time
import random
import socket

from auto_workflow import (
    URL_BASE, PRODUCT_SERVICE_URL, ORDER_SERVICE_URL, WAREHOUSE_SERVICE_URL,
    context
)

# Konfiguracja loggera
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    handlers=[logging.StreamHandler()]
)
logger = logging.getLogger('workflow_e2e_test')


class ArtificialWarehouseSystemE2ETests(unittest.TestCase):

    def setUp(self):
        """Przygotowanie środowiska testowego przed każdym testem"""
        self.session = self.create_optimized_session()
        self.product_id = None
        self.order_id = None
        self.warehouse_entry_id = None

    def create_optimized_session(self):
        """Tworzenie zoptymalizowanej sesji HTTP"""
        session = requests.Session()
        retry_strategy = Retry(
            total=3,
            backoff_factor=0.5,
            status_forcelist=[500, 502, 503, 504],
        )
        adapter = HTTPAdapter(
            pool_connections=10,
            pool_maxsize=20,
            max_retries=retry_strategy
        )
        session.mount("http://", adapter)
        session.mount("https://", adapter)
        return session

    def create_product(self):
        """Tworzenie produktu z weryfikacją"""
        logger.info("KROK 1: Tworzenie produktu")
        url = f"{URL_BASE}{PRODUCT_SERVICE_URL}"

        payload = {
            "name": f"Test_Produkt_{random.randint(1, 1000)}",
            "description": "Produkt testowy utworzony w ramach testu E2E workflow",
            "price": round(random.uniform(10.0, 100.0), 2),
            "quantity": random.randint(10, 50),
            "category": "ELECTRONIC"
        }

        response = self.session.post(url, json=payload)
        response.raise_for_status()

        product_data = response.json()
        self.assertIn("id", product_data, "Odpowiedź nie zawiera ID produktu")

        self.product_id = product_data["id"]
        logger.info(f"✅ Utworzono produkt: {self.product_id}")

        # Weryfikacja poprzez pobranie szczegółów produktu
        self.verify_product_exists(self.product_id)
        return self.product_id

    def verify_product_exists(self, product_id):
        """Weryfikacja istnienia produktu"""
        url = f"{URL_BASE}{PRODUCT_SERVICE_URL}/{product_id}"
        response = self.session.get(url)
        response.raise_for_status()

        product_data = response.json()
        self.assertEqual(product_data["id"], product_id, "ID produktu nie zgadza się")
        logger.info(f"✅ Zweryfikowano istnienie produktu: {product_id}")

    def verify_shipment_created(self):
        """Weryfikacja poprawności utworzenia przesyłki po zamówieniu"""
        logger.info("KROK 2.5: Weryfikacja utworzenia przesyłki")

        if not self.order_id:
            self.fail("Brak zamówienia do weryfikacji przesyłki")

        # Zakładając, że istnieje endpoint do pobierania przesyłki po ID zamówienia
        url = f"{URL_BASE}shipments/order/{self.order_id}"

        try:
            response = self.session.get(url)
            response.raise_for_status()

            shipment_data = response.json()
            self.assertIn("id", shipment_data, "Odpowiedź nie zawiera ID przesyłki")
            self.assertEqual(shipment_data["orderId"], self.order_id, "ID zamówienia w przesyłce nie zgadza się")
            self.assertIn("status", shipment_data, "Przesyłka nie ma statusu")
            self.assertIn("trackingNumber", shipment_data, "Przesyłka nie ma numeru śledzenia")

            self.shipment_id = shipment_data["id"]
            logger.info(f"✅ Zweryfikowano przesyłkę: {self.shipment_id} dla zamówienia: {self.order_id}")
            logger.info(f"   Status przesyłki: {shipment_data['status']}")
            logger.info(f"   Numer śledzenia: {shipment_data['trackingNumber']}")

            return self.shipment_id
        except requests.exceptions.HTTPError as e:
            if e.response.status_code == 404:
                logger.warning(f"Przesyłka dla zamówienia {self.order_id} nie została jeszcze utworzona")
                return None
            else:
                logger.error(f"Błąd podczas weryfikacji przesyłki: {e}")
                raise

    def create_order(self):
        """Tworzenie zamówienia z weryfikacją"""
        logger.info("KROK 2: Tworzenie zamówienia")

        if not self.product_id:
            self.fail("Brak produktu do utworzenia zamówienia")

        url = f"{URL_BASE}{ORDER_SERVICE_URL}"
        payload = {
            "status": "Pending",
            "shippingAddress": "Test E2E - ul. Testowa 10, Warszawa",
            "orderItems": [
                {
                    "id": self.product_id,
                    "quantity": random.randint(1, 5)
                }
            ]
        }

        response = self.session.post(url, json=payload)
        response.raise_for_status()

        order_data = response.json()
        self.assertIn("id", order_data, "Odpowiedź nie zawiera ID zamówienia")

        self.order_id = order_data["id"]
        logger.info(f"✅ Utworzono zamówienie: {self.order_id}")

        # Weryfikacja poprzez pobranie szczegółów zamówienia
        self.verify_order_exists(self.order_id)
        return self.order_id

    def verify_order_exists(self, order_id):
        """Weryfikacja istnienia zamówienia"""
        url = f"{URL_BASE}{ORDER_SERVICE_URL}/{order_id}"
        response = self.session.get(url)
        response.raise_for_status()

        order_data = response.json()
        self.assertEqual(order_data["id"], order_id, "ID zamówienia nie zgadza się")
        self.assertIn("orderItems", order_data, "Zamówienie nie ma pozycji")
        self.assertGreaterEqual(len(order_data["orderItems"]), 1, "Zamówienie powinno mieć co najmniej jedną pozycję")
        logger.info(f"✅ Zweryfikowano istnienie zamówienia: {order_id}")

    def add_product_to_warehouse(self, max_retries=3):
        """Dodawanie produktu do magazynu z ponownych próbami w przypadku błędu"""
        logger.info("KROK 3: Dodawanie produktu do magazynu")

        if not self.product_id:
            self.fail("Brak produktu do dodania do magazynu")

        url = f"{URL_BASE}{WAREHOUSE_SERVICE_URL}"
        payload = {
            "productId": self.product_id,
            "totalQuantity": random.randint(5, 20)
        }

        retries = 0
        while retries < max_retries:
            try:
                response = self.session.post(url, json=payload)
                response.raise_for_status()

                entry_data = response.json()
                logger.info(f"✅ Dodano produkt do magazynu: {entry_data}")

                self.warehouse_entry_id = entry_data.get("id")
                return True
            except Exception as e:
                retries += 1
                if retries == max_retries:
                    logger.error(f"❌ Nie udało się dodać produktu do magazynu po {max_retries} próbach: {e}")
                    return False
                logger.warning(f"Próba {retries}/{max_retries} nie powiodła się: {e}, ponawianie...")
                time.sleep(0.5 + random.uniform(0, 1.0))

    def test_full_client_workflow(self):
        """Test E2E pełnego procesu klienta, bazujący na process_client_workflow"""
        start_time = time.time()
        success = False

        try:
            # Krok 1: Tworzenie produktu
            self.create_product()

            # Opóźnienie między operacjami
            logger.info("Oczekiwanie 1 sekunda...")
            time.sleep(1)

            # Krok 2: Tworzenie zamówienia
            self.create_order()

            # Krok 2.5: Weryfikacja utworzenia przesyłki
            self.verify_shipment_created()

            # Opóźnienie przed operacją na magazynie
            logger.info("Oczekiwanie 1 sekunda...")
            time.sleep(1)

            # Krok 3: Dodanie produktu do magazynu
            success = self.add_product_to_warehouse()

            # Weryfikacja całego workflow
            self.assertTrue(success, "Workflow nie zakończył się sukcesem")
            self.assertIsNotNone(self.product_id, "Brak utworzonego produktu")
            self.assertIsNotNone(self.order_id, "Brak utworzonego zamówienia")

            # Podsumowanie testu
            execution_time = time.time() - start_time
            logger.info(f"✅✅✅ Test workflow zakończony sukcesem w czasie {execution_time:.2f} sekund")

        except Exception as e:
            execution_time = time.time() - start_time
            logger.error(f"❌ Test workflow nie powiódł się: {e}")
            logger.error(f"Czas wykonania: {execution_time:.2f} sekund")
            raise


if __name__ == '__main__':
    unittest.main()