import random
from time import sleep, time
from concurrent.futures import ThreadPoolExecutor
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
import socket

from auto_workflow import create_product, create_order, add_product_to_warehouse, create_warehouse_location, create_multiple_products


# Tworzenie i konfiguracja globalnej sesji HTTP
def create_global_session():
    # Konfiguracja mechanizmów ponownych prób i puli połączeń
    session = requests.Session()
    retry_strategy = Retry(
        total=3,
        backoff_factor=0.5,
        status_forcelist=[500, 502, 503, 504],
    )
    adapter = HTTPAdapter(
        pool_connections=25,
        pool_maxsize=100,
        max_retries=retry_strategy,
        # Opcje umożliwiające szybsze ponowne użycie socketów
        socket_options=[(socket.SOL_SOCKET, socket.SO_KEEPALIVE, 1),
                        (socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)]
    )
    session.mount("http://", adapter)
    session.mount("https://", adapter)
    return session


# Podmiana domyślnej sesji w requests
requests.Session = create_global_session


def add_product_with_retry(max_retries=3):
    retries = 0
    while retries < max_retries:
        try:
            add_product_to_warehouse()
            return True
        except Exception as e:
            retries += 1
            if retries == max_retries:
                print(f"Nie udało się dodać produktu po {max_retries} próbach: {e}")
                return False
            print(f"Próba {retries}/{max_retries} nie powiodła się: {e}, ponawianie...")
            sleep(0.5 + random.uniform(0, 1.0))  # Opóźnienie z losowym komponentem


def process_client_workflow():
    try:
        create_product()
        sleep(1)  # Małe opóźnienie między operacjami
        create_order()
        sleep(1)  # Większe opóźnienie przed operacją na magazynie
        return add_product_with_retry()
    except Exception as e:
        print(f"Błąd podczas przetwarzania: {e}")
        return False

def process_client_workflow_many_product_with_one_request():
    try:
        create_multiple_products(count=random.randint(5, 1000))
        sleep(1)
        return True
    except Exception as e:
        print(f"Błąd podczas przetwarzania: {e}")
        return False

def process_client_workflow_many_product_with_many_request():
    try:
        core = random.randint(1, 50)
        if core == 5 or core == 6:
            create_multiple_products(count=random.randint(5, 1000))
        else:
            create_product()
        sleep(1)
        return True
    except Exception as e:
        print(f"Błąd podczas przetwarzania: {e}")
        return False


if __name__ == '__main__':
    # Monkeypatching sesji HTTP - musi nastąpić przed użyciem funkcji z auto_workflow
    start_time = time()
    requests.Session = create_global_session

    # Najpierw utwórz lokalizację magazynową (jednokrotnie)
    create_warehouse_location()

    # Parametry symulacji
    NUM_CLIENTS = 50  # Zmniejszono z 100000 dla początkowych testów
    MAX_CONCURRENT = 30  # Zmieniono z 1 na 5 dla lepszej wydajności

    print(f"Uruchamianie symulacji z {NUM_CLIENTS} klientami ({MAX_CONCURRENT} jednocześnie)...")

    # Użycie ThreadPoolExecutor do równoległego wykonania zadań
    with ThreadPoolExecutor(max_workers=MAX_CONCURRENT) as executor:
        # Uruchomienie zadań
        results = list(executor.map(lambda _: process_client_workflow_many_product_with_many_request(), range(NUM_CLIENTS)))

    # Podsumowanie
    successful = results.count(True)
    failed = results.count(False)
    print(f"Zakończono symulację: {successful} udanych operacji, {failed} błędów")
    print(f"Czas wykonania symulacji: {time() - start_time:.2f} sekund")