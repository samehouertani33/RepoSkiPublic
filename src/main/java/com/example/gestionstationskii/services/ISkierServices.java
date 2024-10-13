package com.example.gestionstationskii.services;

import com.example.gestionstationskii.entities.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ISkierServices {

	Skier updateSkier(Skier skier);

	List<Skier> retrieveAllSkiers();

	Skier addSkier(Skier skier);

	Skier assignSkierToSubscription(Long numSkier, Long numSubscription);

	Skier addSkierAndAssignToCourse(Skier skier, Long numCourse);

	void removeSkier(Long numSkier);

	Skier retrieveSkier(Long numSkier);

	Skier assignSkierToPiste(Long numSkieur, Long numPiste);

	List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription);

	// Compte le nombre de skieurs par couleur de piste
	HashMap<Color, Integer> nombreSkiersParColorPiste();

	// Récupère les skieurs dont l'abonnement expire bientôt
	List<Skier> getSkiersBySubscriptionExpiry();

	// Récupère les skieurs qui ont souscrit un abonnement entre deux dates spécifiées
	List<Skier> getSkiersBySubscriptionPeriod(LocalDate startDate, LocalDate endDate);

	// Calcule le nombre moyen de skieurs par type d'abonnement

	// Recherche des skieurs qui ont accès à des pistes d'une certaine plage de longueur
	List<Skier> findSkiersInRangeOfPistes(int minLength, int maxLength);

	// Assigne plusieurs skieurs à une même piste en une seule opération
	List<Skier> assignMultipleSkiersToPiste(List<Long> skierIds, Long numPiste);

	// Récupère des statistiques sur les skieurs
	Map<String, Object> getSkierStatistics();
	Map<TypeSubscription, Double> calculateAverageSkiersPerSubscription();
}
