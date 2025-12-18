# script_ia.py
import sys
import time

time.sleep(1)

if len(sys.argv) > 1:
    filename = sys.argv[1]
    print(f"Analyse terminee pour {filename}: COUCOU CA MARCHE !")
else:
    print("Erreur: Aucun fichier fourni")