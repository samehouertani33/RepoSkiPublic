package com.example.gestionstationskii.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SkierServicesImpl implements ISkierServices {

    private ISkierRepository skierRepository;

    private IPisteRepository pisteRepository;

    private ICourseRepository courseRepository;

    private IRegistrationRepository registrationRepository;

    private ISubscriptionRepository subscriptionRepository;


    // Add Skier
    @Override
    public Skier addSkier(Skier skier) {
        switch (skier.getSubscription().getTypeSub()) {
            case ANNUAL:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusYears(1));
                break;
            case SEMESTRIEL:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(6));
                break;
            case MONTHLY:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(1));
                break;
        }
        return skierRepository.save(skier);
    }

    // Update Skier
    @Override
    public Skier updateSkier(Skier skier) {
        return skierRepository.save(skier);
    }

    // Delete Skier
    @Override
    public void removeSkier(Long numSkier) {
        skierRepository.deleteById(numSkier);
    }

    @Override
    public Skier assignSkierToSubscription(Long numSkier, Long numSubscription) {
        Skier skier = skierRepository.findById(numSkier).orElse(null);
        Subscription subscription = subscriptionRepository.findById(numSubscription).orElse(null);
        skier.setSubscription(subscription);
        return skierRepository.save(skier);
    }

    // Affichage d'un Skier
    @Override
    public Skier retrieveSkier(Long numSkier) {
        return skierRepository.findById(numSkier).orElse(null);
    }

    // Affichae de tous les Skiers
    @Override
    public List<Skier> retrieveAllSkiers() {
        return skierRepository.findAll();
    }

    // Ajouter un nouveau skieur et l'affecter à un cours.
    /*@Override
    public Skier addSkierAndAssignToCourse(Skier skier, Long numCourse) {
        Skier savedSkier = skierRepository.save(skier);
        Course course = courseRepository.getById(numCourse);
        Set<Registration> registrations = savedSkier.getRegistrations();
        for (Registration r : registrations) {
            r.setSkier(savedSkier);
            r.setCourse(course);
            registrationRepository.save(r);
        }
        return savedSkier;
    }*/

    @Override
    public Skier addSkierAndAssignToCourse(Skier skier, Long numCourse) {
        Skier savedSkier = skierRepository.save(skier);
        Course course = courseRepository.findById(numCourse).orElse(null);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        for (Registration r : savedSkier.getRegistrations()) {
            if (!r.getCourse().equals(course)) { // Vérifiez si le cours est déjà enregistré
                r.setSkier(savedSkier);
                r.setCourse(course);
                registrationRepository.save(r);
            }
        }
        return savedSkier;
    }

    // Affecter un skieur existant à une piste.
    @Override
    public Skier assignSkierToPiste(Long numSkieur, Long numPiste) {
        Skier skier = skierRepository.findById(numSkieur).orElse(null);
        Piste piste = pisteRepository.findById(numPiste).orElse(null);
        try {
            skier.getPistes().add(piste);
        } catch (NullPointerException exception) {
            Set<Piste> pisteList = new HashSet<>();
            pisteList.add(piste);
            skier.setPistes(pisteList);
        }
        return skierRepository.save(skier);
    }

    // Récupérer une liste de skieurs en fonction de leur type d'abonnement.
    @Override
    public List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription) {
        return skierRepository.findBySubscription_TypeSub(typeSubscription);
    }

    /*
    Les méthodes eli sous cette lignes ena eli zedettehom mahenouch mawjoudin
    */

    // Compte le nombre de skieurs par couleur de piste
    @Override
    public HashMap<Color, Integer> nombreSkiersParColorPiste() {
        HashMap<Color, Integer> nombreSkiersParColorPiste = new HashMap<>();
        Color[] colors = Color.values();
        for (Color c : colors) {
            nombreSkiersParColorPiste.put(c, skierRepository.skiersByColorPiste(c).size());
        }
        return nombreSkiersParColorPiste;
    }

    // Retourne les skieurs dont l'abonnement expire bientôt (dans un mois)
    @Override
    public List<Skier> getSkiersBySubscriptionExpiry() {
        LocalDate today = LocalDate.now(); //La date actuelle
        LocalDate nextMonth = today.plusMonths(1); //La date un mois plus tard
        return skierRepository.findAll().stream()
                .filter(skier -> {
                    Subscription subscription = skier.getSubscription(); //Pour chaque skieur, on récupère son abonnement
                    return subscription != null && //On vérifie si l'abonnement n'est pas null
                            subscription.getEndDate().isBefore(nextMonth) && //La date de fin de l'abonnement (endDate) est avant nextMonth
                            subscription.getEndDate().isAfter(today); //La date de fin de l'abonnement est après today.
                })
                .collect(Collectors.toList());
    }

    // Retourne les skieurs qui ont souscrit un abonnement entre deux dates spécifiées
    @Override
    public List<Skier> getSkiersBySubscriptionPeriod(LocalDate startDate, LocalDate endDate) {
        return skierRepository.findAll().stream()
                .filter(skier -> {
                    Subscription subscription = skier.getSubscription();
                    return subscription != null &&
                            !subscription.getEndDate().isBefore(startDate) && // Fin après ou à la même date que startDate
                            !subscription.getStartDate().isAfter(endDate);    // Début avant ou à la même date que endDate
                })
                .collect(Collectors.toList());
    }


    // Calcule le nombre moyen de skieurs par type d'abonnement
    @Override
    public Map<TypeSubscription, Double> calculateAverageSkiersPerSubscription() {
        List<Skier> skiers = skierRepository.findAll();
        Map<TypeSubscription, Integer> countMap = new HashMap<>();
        Map<TypeSubscription, Integer> totalMap = new HashMap<>();

        for (Skier skier : skiers) {
            if (skier.getSubscription() != null) {
                TypeSubscription type = skier.getSubscription().getTypeSub();
                countMap.put(type, countMap.getOrDefault(type, 0) + 1);
                totalMap.put(type, totalMap.getOrDefault(type, 0) + skier.getPistes().size()); // Nombre total de pistes
            }
        }

        Map<TypeSubscription, Double> averageMap = new HashMap<>();
        for (TypeSubscription type : countMap.keySet()) {
            double average = (double) totalMap.get(type) / countMap.get(type);
            averageMap.put(type, average);
        }
        return averageMap;
    }

    // Recherche des skieurs qui ont accès à des pistes d'une certaine plage de longueur
    @Override
    public List<Skier> findSkiersInRangeOfPistes(int minLength, int maxLength) {
        List<Piste> pistesInRange = pisteRepository.findAll().stream()
                .filter(piste -> piste.getLength() >= minLength && piste.getLength() <= maxLength)
                .collect(Collectors.toList());

        return pistesInRange.stream()
                .flatMap(piste -> piste.getSkiers().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    // Assigner plusieurs skieurs à une même piste en une seule opération
    @Override
    public List<Skier> assignMultipleSkiersToPiste(List<Long> skierIds, Long numPiste) {
        Piste piste = pisteRepository.findById(numPiste).orElse(null);
        if (piste == null) {
            throw new IllegalArgumentException("Piste not found");
        }
        List<Skier> updatedSkiers = new ArrayList<>();
        for (Long skierId : skierIds) {
            Skier skier = skierRepository.findById(skierId).orElse(null);
            if (skier != null) {
                if (!skier.getPistes().contains(piste)) {
                    skier.getPistes().add(piste);
                    updatedSkiers.add(skierRepository.save(skier));
                }
            }
        }
        return updatedSkiers;
    }

    // Statistiques des skieurs, y compris le nombre total et par type d'abonnement
    @Override
    public Map<String, Object> getSkierStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        List<Skier> skiers = skierRepository.findAll();

        // Total des skieurs
        statistics.put("totalSkiers", skiers.size());

        // Calculer le nombre de skieurs par type d'abonnement
        Map<TypeSubscription, Integer> subscriptionCount = new HashMap<>();
        for (Skier skier : skiers) {
            if (skier.getSubscription() != null) {
                TypeSubscription type = skier.getSubscription().getTypeSub();
                subscriptionCount.put(type, subscriptionCount.getOrDefault(type, 0) + 1);
            }
        }
        statistics.put("skiersBySubscription", subscriptionCount);

        // Calculer le nombre de skieurs par couleur de piste
        Map<Color, Integer> colorCount = new HashMap<>();
        for (Skier skier : skiers) {
            for (Piste piste : skier.getPistes()) {
                colorCount.put(piste.getColor(), colorCount.getOrDefault(piste.getColor(), 0) + 1);
            }
        }
        statistics.put("skiersByColor", colorCount);

        return statistics;
    }


}
