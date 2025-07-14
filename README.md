### ♻️ Recommandations Green Code

Voici quelques pistes d’amélioration pour rendre ce projet plus économe en ressources :

#### 1. Fusionner les microservices en un monolithe

> Pour une application de petite taille, l’architecture microservices génère des appels HTTP internes, de la consommation mémoire et CPU inutile.  
> Une version monolithique réduirait :
> - le nombre de containers à lancer,
> - la consommation réseau interne,
> - la complexité de déploiement.

#### 2. Mettre en place un cache local pour les évaluations

> L’évaluation du risque de diabète est déterministe. Si on appelle plusieurs fois l’évaluation pour un même patient, le résultat sera identique tant que les données n’ont pas changé.  
> Un cache mémoire léger (ex. [Caffeine](https://github.com/ben-manes/caffeine)) permettrait d’éviter les appels et traitements redondants.

#### 3. Supprimer les dépendances inutilisées

> Vérifier que seules les bibliothèques réellement utilisées (Jackson, Lombok, etc.) sont présentes pour réduire la taille des images Docker et des builds Maven.

#### 4. Optimiser les accès à la base de données

> - Ajouter des index sur les champs `patId`,
> - Limiter les projections aux champs utiles (plutôt que charger tout le document MongoDB),
> - Éviter les requêtes multiples pour une même donnée.

#### 5. Utiliser des images Docker plus légères

> Utiliser les variantes `-slim` ou `Alpine` des images de base (ex. `openjdk:17-slim`) pour réduire l’empreinte disque et les temps de démarrage.
