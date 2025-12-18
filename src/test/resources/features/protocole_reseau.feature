# language: fr

Fonctionnalité: Robustesse du protocole réseau
En tant qu'administrateur système
Je veux que le serveur rejette les données mal formées
Pour éviter les blocages de ressources

Scénario: Déconnexion brutale du client
Quand je connecte un socket au serveur
Et que j'envoie la taille "500000" (bytes)
Mais que je coupe la connexion après avoir envoyé seulement 10 bytes
Alors le serveur doit détecter la fin de flux prématurée
Et le fichier partiel doit être libéré (stream fermé)
Et le serveur doit imprimer une erreur de communication dans les logs