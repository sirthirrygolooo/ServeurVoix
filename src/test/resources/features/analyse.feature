Feature: Analyse Audio SamSoul

  Scenario: Analyse d'un fichier audio silencieux
    Given le service d'analyse est prêt
    When je traite un fichier audio contenant du silence
    Then le résultat doit indiquer un ratio de silence supérieur à 0.9
    And le résultat ne doit pas être null