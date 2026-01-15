import os
import time
import json
from pathlib import Path
from PIL import Image
from google import genai
from google.genai import types

# --- 1. CONFIGURAZIONE PERCORSI ---

# Percorso assoluto del file JSON
json_source_path = Path(r"C:\Users\Ale\Documents\GitHub\AppCantiere\app\src\main\assets\tools_data.json")

# Calcola il percorso della cartella drawable relativo al JSON
# Se il json è in .../app/src/main/assets, risaliamo per trovare .../app/src/main/res/drawable
project_root = json_source_path.parents[4]
drawable_folder = project_root / "app" / "src" / "main" / "res" / "drawable"

# Crea la cartella se non esiste
drawable_folder.mkdir(parents=True, exist_ok=True)

print(f"File JSON sorgente: {json_source_path}")
print(f"Salvataggio immagini in: {drawable_folder}")

# --- 2. CARICAMENTO DATI JSON ---

if not json_source_path.exists():
    print(f"ERRORE: File JSON non trovato in {json_source_path}")
    exit()

try:
    with open(json_source_path, 'r', encoding='utf-8') as f:
        data_json = json.load(f)
except Exception as e:
    print(f"ERRORE lettura JSON: {e}")
    exit()

# Gestione struttura JSON (se c'è una chiave root o è una lista diretta)
categories = data_json.get("glossario_cantiere", data_json) if isinstance(data_json, dict) else data_json

# --- 3. INIZIALIZZAZIONE CLIENT ---
client = genai.Client()

print(f"\nInizio generazione...\n")

# --- 4. CICLO DI GENERAZIONE ---

for categoria in categories:
    nome_cat = categoria.get("categoria", "Senza Categoria")
    elementi = categoria.get("elementi", [])

    print(f"--- Elaborazione Categoria: {nome_cat} ---")

    for item in elementi:
        # Pulisci il nome dell'oggetto dal JSON
        obj = item.strip()

        # --- LOGICA RICHIESTA (ADATTATA AL CICLO) ---

        # Android richiede nomi di file minuscoli, senza spazi (usa underscore)
        safe_obj_name = obj.lower().replace(" ", "_").replace("/", "_").replace("'", "")
        filename = f"{safe_obj_name}.png"
        target_file_path = drawable_folder / filename

        # Controllo se esiste già per risparmiare tempo/token
        if target_file_path.exists():
            print(f"Saltato (esiste già): {filename}")
            continue

        print(f"Generazione immagine per '{obj}' in corso...")



print("\n--- TUTTO COMPLETATO ---")
