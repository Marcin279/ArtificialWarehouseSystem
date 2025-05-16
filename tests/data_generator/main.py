from time import sleep
from concurrent.futures import ThreadPoolExecutor

from auto_workflow import create_product, create_order, create_warehouse_location, add_product_to_warehouse

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
            sleep(1)  # Krótkie opóźnienie przed kolejną próbą

def process_client_workflow():
    try:
        create_product()
        create_order()
        add_product_with_retry()
        return True
    except Exception as e:
        print(f"Błąd podczas przetwarzania: {e}")
        return False


if __name__ == '__main__':
    # Najpierw utwórz lokalizację magazynową (jednokrotnie)
    create_warehouse_location()

    # Parametry symulacji
    NUM_CLIENTS = 1000  # Liczba klientów (zadań)
    MAX_CONCURRENT = 2  # Maksymalna liczba jednoczesnych klientów (wątków)

    print(f"Uruchamianie symulacji z {NUM_CLIENTS} klientami ({MAX_CONCURRENT} jednocześnie)...")

    # Użycie ThreadPoolExecutor do równoległego wykonania zadań
    with ThreadPoolExecutor(max_workers=MAX_CONCURRENT) as executor:
        # Uruchomienie zadań
        results = list(executor.map(lambda _: process_client_workflow(), range(NUM_CLIENTS)))

    # Podsumowanie
    successful = results.count(True)
    failed = results.count(False)
    print(f"Zakończono symulację: {successful} udanych operacji, {failed} błędów")