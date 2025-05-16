import random

import requests
from enum import Enum

URL_BASE = "http://localhost:8080/api/"

PRODUCT_SERVICE_URL = "products"
ORDER_SERVICE_URL = "orders"
WAREHOUSE_SERVICE_URL = "warehouse/inventory"
WAREHOUSE_LOCATION_URL = "warehouse/locations"

context = {}

class ProductCategory(Enum):
    ELECTRONIC = 1
    CLOTHING = 2
    FOOD = 3
    TOOLS = 4
    OTHER = 5


def create_product():
    url = f"{URL_BASE}{PRODUCT_SERVICE_URL}"
    payload = {
        "name": f"Produkt_{random.randint(1, 1000)}",
        "description": " ".join(random.choices([
            "Innowacyjny produkt, który zmienia zasady gry.",
            "Najwyższa jakość wykonania i niezawodność.",
            "Idealny wybór dla wymagających użytkowników.",
            "Produkt, który łączy funkcjonalność z estetyką.",
            "Nowoczesne rozwiązanie dla codziennych potrzeb.",
            "Wyjątkowy design i zaawansowana technologia.",
            "Twój nowy ulubiony produkt w tej kategorii.",
            "Stworzony z myślą o Twojej wygodzie i komforcie."
        ], k=random.randint(1, 2))),
        "price": round(random.uniform(10.0, 100.0), 2),
        "quantity": random.randint(1, 500),
        "category": random.choice(list(ProductCategory)).name,
    }
    response = requests.post(url, json=payload)
    response.raise_for_status()
    product_id = response.json().get("id")
    context["product_id"] = product_id
    print(f"📦 Utworzono produkt: {product_id}")

def create_order():
    url = f"{URL_BASE}{ORDER_SERVICE_URL}"
    payload = {
        "status": "Pending",
        "shippingAddress": "Sulkowice Szkolna 20",
        "orderItems": [
            {
                "id": context["product_id"],
                "quantity": 10
            }
        ]
    }
    response = requests.post(url, json=payload)
    response.raise_for_status()
    order_id = response.json().get("id")
    context["order_id"] = order_id
    print(f"🧾 Utworzono zamówienie: {order_id}")

def create_warehouse_location():
    url = f"{URL_BASE}{WAREHOUSE_LOCATION_URL}"
    payload = {
            "section": random.choice(["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P"]),
            "shelf": str(random.randint(1, 10)),
            "bin": random.choice(["A1", "B2", "C3", "D4"]),
            "capacity": random.randint(50, 200)
    }
    response = requests.post(url, json=payload)
    response.raise_for_status()
    location_id = response.json().get("id")
    print(f"📍 Utworzono lokalizację magazynową: {location_id}")

def add_product_to_warehouse():
    url = f"{URL_BASE}{WAREHOUSE_SERVICE_URL}"
    payload = {
        "productId": context["product_id"],
        "totalQuantity": random.randint(1, 100)
    }
    response = requests.post(url, json=payload)
    response.raise_for_status()
    print(f"📦 Dodano produkt do lokalizacji magazynowej: {context['product_id']}")

def create_warehouse():
    url = f"{URL_BASE}{WAREHOUSE_SERVICE_URL}"
    payload = {
        "orderId": context["order_id"],
        "address": "ul. Testowa 123, Kraków"
    }
    response = requests.post(url, json=payload)
    response.raise_for_status()
    shipment_id = response.json().get("id")
    print(f"🚚 Utworzono wysyłkę: {shipment_id}")
