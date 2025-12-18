Fonctionnalité: Traitement de commande vocale via IA
En tant qu'application mobile
Je veux envoyer un fichier audio brut au serveur
Pour obtenir une transcription ou une action en retour

Contexte:
Étant donné que le serveur Java est démarré sur le port 65432
Et que le script "script_ia.py" est présent à la racine
Et que le dossier "received_audio/" est accessible en écriture

Scénario: Flux nominal complet (Envoi -> Analyse -> Réponse)
Quand je connecte un socket TCP au serveur
Et que j'envoie la taille du fichier (Long) suivie des octets du fichier "test.wav"
Alors le serveur doit sauvegarder le fichier dans "received_audio/"
Et le serveur doit exécuter le script Python avec ce fichier en argument
Et je dois recevoir en retour une chaîne de caractères contenant "Analyse terminée"
Et la connexion doit être fermée proprement

Scénario: Gestion de la concurrence (Thread Pool)
Quand 5 clients envoient simultanément un fichier audio
Alors le serveur doit accepter toutes les connexions sans rejeter
Et 5 fichiers distincts doivent apparaître dans le dossier de sauvegarde
Et chaque client doit recevoir sa propre réponse d'analyse unique

Scénario: Erreur du script IA
Étant donné que le script Python est corrompu ou manquant
Quand j'envoie un fichier audio valide
Alors je dois recevoir une réponse commençant par "Erreur" ou "Erreur Interne"
Mais le serveur ne doit pas planter et rester disponible pour d'autres clients