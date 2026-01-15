import os
import time
import json
from pathlib import Path
from PIL import Image
from google import genai
from google.genai import types

#json_source_path = Path(r"C:\Users\Ale\Documents\GitHub\AppCantiere\app\src\main\assets\tools_data.json")
json_source_path = Path(r"C:\Users\Ale\Documents\GitHub\AppCantiere\gen\fix.json")

project_root = json_source_path.parents[4]
drawable_folder = project_root / "app" / "src" / "main" / "res" / "drawable"

drawable_folder.mkdir(parents=True, exist_ok=True)

print(f"File JSON sorgente: {json_source_path}")
print(f"Salvataggio immagini in: {drawable_folder}")

if not json_source_path.exists():
    print(f"ERRORE: File JSON non trovato in {json_source_path}")
    exit()

try:
    with open(json_source_path, 'r', encoding='utf-8') as f:
        data_json = json.load(f)
except Exception as e:
    print(f"ERRORE lettura JSON: {e}")
    exit()

categories = data_json.get("glossario_cantiere", data_json) if isinstance(data_json, dict) else data_json

client = genai.Client()

print(f"\nInizio generazione...\n")

for categoria in categories:
    nome_cat = categoria.get("categoria", "Senza Categoria")
    elementi = categoria.get("elementi", [])

    print(f"--- Elaborazione Categoria: {nome_cat} ---")

    for item in elementi:
        obj = item.strip()
        safe_obj_name = obj.lower().replace(" ", "_").replace("/", "_").replace("'", "")
        filename = f"{safe_obj_name}.png"
        target_file_path = drawable_folder / filename

        if target_file_path.exists():
            print(f"Saltato (esiste già): {filename}")
            continue

        print(f"Generazione immagine per '{obj}' in corso...")

        try:
            prompt = (f"Crea un immagine realistica di {obj} dimensione 512x512, sfondo bianco. CONTESTO: Cantiere/costruzione/edilizia, CATEGORIA: {nome_cat}. Non includere parole, design moderni NON FUTURISTICI. Non prendere i nomi alla lettera, pensa alla loro applicazione in un cantiere. Fai occupare agli oggetti il piu spazio dell'immagine. Se la categoria è tipologie_costruzioni, rappresenta gli edifici come completati. Non mettere decorazioni o sfondi inutili al dilà dell'oggetto richiesto")

            response = client.models.generate_content(
                model="gemini-2.5-flash-image",
                contents=[prompt],
            )

            generated = False
            for part in response.parts:
                if part.text is not None:

                    print(f"INFO (Testo ricevuto): {part.text}")

                if part.inline_data is not None:
                    image = part.as_image()
                    image.save(f"{target_file_path}")
                    print(f"SUCCESSO!")
                    print(f"Immagine salvata in: {target_file_path}")
                    generated = True

            if not generated:
                print("\nERRORE: Il modello non ha generato alcuna immagine (possibile blocco sicurezza o formato inatteso).")

            time.sleep(10)

        except Exception as e:
            print(f"\nERRORE CRITICO durante la chiamata API per '{obj}': {e}")
            continue

print("\n--- TUTTO COMPLETATO ---")
