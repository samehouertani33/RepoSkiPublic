package com.example.gestionstationskii;


import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.gestionstationskii.services.SkierServicesImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkierServicesImplMockTest {

    @InjectMocks
    private SkierServicesImpl skierServices;

    @Mock
    ISkierRepository skierRepository;
    @Mock
    IPisteRepository pisteRepository;
    @Mock
    ICourseRepository courseRepository;

    //Déclaration de deux objets (skier et piste) de type Skier et Piste
    Skier skier = new Skier();
    Piste piste = new Piste();

    @Test
    public void testRemoveSkier() {
        Long skierId = 1L;

        skierServices.removeSkier(skierId);

        verify(skierRepository).deleteById(skierId);
    }

    // Test pour assignSkierToPiste
    @Test
    public void testAssignSkierToPiste() {
        Long skierId = 1L;
        Long pisteId = 2L;

        Skier skier = new Skier();
        skier.setNumSkier(skierId);
        Piste piste = new Piste();
        piste.setNumPiste(pisteId);

        when(skierRepository.findById(skierId)).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(pisteId)).thenReturn(Optional.of(piste));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Skier result = skierServices.assignSkierToPiste(skierId, pisteId);

        assertNotNull(result);
        assertTrue(result.getPistes().contains(piste));
        verify(skierRepository).save(skier);
    }

    @Test // Annotation indiquant que cette méthode est un test JUnit
    void testNombreSkiersParColorPiste() {
        // Préparer les résultats attendus
        Map<Color, Integer> expectedColorCount = new EnumMap<>(Color.class);
        expectedColorCount.put(Color.RED, 3); // 3 skieurs sur pistes rouges
        expectedColorCount.put(Color.BLUE, 1); // 1 skieur sur piste bleue
        expectedColorCount.put(Color.GREEN, 0); // 0 skieurs sur piste verte
        expectedColorCount.put(Color.BLACK, 2); // 2 skieurs sur piste noire

        // Simuler les retours du repository pour chaque couleur
        when(skierRepository.skiersByColorPiste(Color.RED))
                .thenReturn(Arrays.asList(new Skier(), new Skier(), new Skier())); // 3 skieurs sur rouge
        when(skierRepository.skiersByColorPiste(Color.BLUE))
                .thenReturn(Collections.singletonList(new Skier())); // 1 skieur sur bleu
        when(skierRepository.skiersByColorPiste(Color.GREEN))
                .thenReturn(new ArrayList<>()); // Aucun skieur sur vert
        when(skierRepository.skiersByColorPiste(Color.BLACK))
                .thenReturn(Arrays.asList(new Skier(), new Skier())); // 2 skieurs sur noir

        // Appeler la méthode que l'on teste
        Map<Color, Integer> actualResult = skierServices.nombreSkiersParColorPiste();

        // Vérifier que le résultat correspond aux attentes
        assertEquals(expectedColorCount, actualResult,
                "Échec du test : le nombre de skieurs par couleur de piste ne correspond pas aux attentes." +
                        "\nRésultat obtenu : " + actualResult +
                        "\nRésultat attendu : " + expectedColorCount);

        // Message explicatif supplémentaire en cas de succès
        System.out.println("Test validé : le nombre de skieurs pour chaque couleur de piste est conforme aux attentes.");

        // Vérifier que chaque appel au repository a été fait exactement une fois pour chaque couleur
        Mockito.verify(skierRepository, Mockito.times(1)).skiersByColorPiste(Color.RED);
        System.out.println("Vérification réussie : 'skiersByColorPiste' a été appelée pour la piste rouge.");

        Mockito.verify(skierRepository, Mockito.times(1)).skiersByColorPiste(Color.BLUE);
        System.out.println("Vérification réussie : 'skiersByColorPiste' a été appelée pour la piste bleue.");

        Mockito.verify(skierRepository, Mockito.times(1)).skiersByColorPiste(Color.GREEN);
        System.out.println("Vérification réussie : 'skiersByColorPiste' a été appelée pour la piste verte.");

        Mockito.verify(skierRepository, Mockito.times(1)).skiersByColorPiste(Color.BLACK);
        System.out.println("Vérification réussie : 'skiersByColorPiste' a été appelée pour la piste noire.");
    }

    @Test
    void testGetSkiersBySubscriptionExpiry() {
        // Préparer des skieurs avec différents abonnements
        Skier skier1 = new Skier();
        skier1.setFirstName("Jean");
        skier1.setLastName("Dupont");
        skier1.setCity("Paris");
        skier1.setDateOfBirth(LocalDate.of(1990, 5, 15)); // Exemple de date de naissance
        Subscription subscription1 = new Subscription();
        // Abonnement 1 expire dans 15 jours
        subscription1.setStartDate(LocalDate.now().minusDays(10)); // Commencé il y a 10 jours
        subscription1.setEndDate(LocalDate.now().plusDays(15)); // Fin dans 15 jours
        subscription1.setTypeSub(TypeSubscription.ANNUAL); // Type d'abonnement : ANNUEL
        skier1.setSubscription(subscription1); // Associer l'abonnement au skieur 1

        Skier skier2 = new Skier();
        skier2.setFirstName("Marie");
        skier2.setLastName("Curie");
        skier2.setCity("Tunis");
        skier2.setDateOfBirth(LocalDate.of(1985, 7, 30)); // Exemple de date de naissance
        Subscription subscription2 = new Subscription();
        // Abonnement 2 expire dans 25 jours
        subscription2.setStartDate(LocalDate.now().minusDays(5)); // Commencé il y a 5 jours
        subscription2.setEndDate(LocalDate.now().plusDays(25)); // Fin dans 25 jours
        subscription2.setTypeSub(TypeSubscription.MONTHLY); // Type d'abonnement : MENSUEL
        skier2.setSubscription(subscription2); // Associer l'abonnement au skieur 2

        Skier skier3 = new Skier();
        skier3.setFirstName("Albert");
        skier3.setLastName("Einstein");
        skier3.setCity("Berlin");
        skier3.setDateOfBirth(LocalDate.of(1879, 3, 14)); // Exemple de date de naissance
        Subscription subscription3 = new Subscription();
        // Abonnement 3 est déjà expiré
        subscription3.setStartDate(LocalDate.now().minusDays(15)); // Commencé il y a 15 jours
        subscription3.setEndDate(LocalDate.now().minusDays(1)); // Déjà expiré depuis 1 jour
        subscription3.setTypeSub(TypeSubscription.SEMESTRIEL); // Type d'abonnement : SEMESTRIEL
        skier3.setSubscription(subscription3); // Associer l'abonnement au skieur 3

        // Simuler la liste des skieurs retournée par le repository
        Mockito.when(skierRepository.findAll()).thenReturn(Arrays.asList(skier1, skier2, skier3));

        // Appeler la méthode que l'on teste : obtenir les skieurs dont l'abonnement expire bientôt
        List<Skier> result = skierServices.getSkiersBySubscriptionExpiry();

        // Vérifier que le résultat contient uniquement skier1 et skier2
        Assertions.assertEquals(2, result.size(), "Le nombre de skieurs dont l'abonnement expire bientôt est incorrect. " +
                "Résultat attendu : 2, Résultat obtenu : " + result.size());

        assertTrue(result.contains(skier1), "Erreur : Le skieur 1 ne devrait pas être absent de la liste des skieurs. " +
                "Détails : " + skier1);

        assertTrue(result.contains(skier2), "Erreur : Le skieur 2 ne devrait pas être absent de la liste des skieurs. " +
                "Détails : " + skier2);

        assertFalse(result.contains(skier3), "Erreur : Le skieur 3 ne devrait pas être dans la liste des skieurs. " +
                "Détails : " + skier3);

        // Message explicatif en cas de succès du test
        System.out.println("Test réussi : les skieurs dont l'abonnement expire bientôt ont été correctement identifiés.");
        System.out.println("Skieurs identifiés dont l'abonnement expire bientôt :");
        result.forEach(skier -> {
            System.out.println("  - Nom: " + skier.getFirstName() + " " + skier.getLastName());
            System.out.println("    Ville: " + skier.getCity());
            System.out.println("    Date de naissance: " + skier.getDateOfBirth());
            System.out.println("    Type d'abonnement: " + skier.getSubscription().getTypeSub());
            System.out.println("    Abonnement: de " + skier.getSubscription().getStartDate() + " à " + skier.getSubscription().getEndDate());
            System.out.println("    ----------------------------------------");
        });
    }

    @Test
    void testGetSkiersBySubscriptionPeriod() {
        // Définir une période de test
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 5, 31);

        // Préparer des skieurs avec différents abonnements
        Skier skier1 = new Skier();
        skier1.setFirstName("Jean");
        skier1.setLastName("Dupont");
        skier1.setCity("Paris");
        skier1.setDateOfBirth(LocalDate.of(1990, 5, 15));
        Subscription subscription1 = new Subscription();
        subscription1.setStartDate(LocalDate.of(2024, 2, 10));
        subscription1.setEndDate(LocalDate.of(2024, 4, 15));
        skier1.setSubscription(subscription1);

        Skier skier2 = new Skier();
        skier2.setFirstName("Marie");
        skier2.setLastName("Curie");
        skier2.setCity("Tunis");
        skier2.setDateOfBirth(LocalDate.of(1985, 7, 30));
        Subscription subscription2 = new Subscription();
        subscription2.setStartDate(LocalDate.of(2024, 1, 1));
        subscription2.setEndDate(LocalDate.of(2024, 2, 20));
        skier2.setSubscription(subscription2);

        Skier skier3 = new Skier();
        skier3.setFirstName("Albert");
        skier3.setLastName("Einstein");
        skier3.setCity("Berlin");
        skier3.setDateOfBirth(LocalDate.of(1879, 3, 14));
        Subscription subscription3 = new Subscription();
        subscription3.setStartDate(LocalDate.of(2024, 6, 1));
        subscription3.setEndDate(LocalDate.of(2024, 7, 1));
        skier3.setSubscription(subscription3);

        // Simuler la liste des skieurs retournée par le repository
        when(skierRepository.findAll()).thenReturn(Arrays.asList(skier1, skier2, skier3));

        // Appeler la méthode que l'on teste
        List<Skier> result = skierServices.getSkiersBySubscriptionPeriod(startDate, endDate);

        // Vérifier que le résultat contient uniquement skier1 et skier2
        assertEquals(2, result.size(), "Le nombre de skieurs dans la période est incorrect.");

        // Vérifier les skieurs retournés
        assertEquals(skier1, result.get(0), "Le skieur 1 devrait être dans la liste.");
        assertEquals(skier2, result.get(1), "Le skieur 2 devrait être dans la liste.");

        // Afficher les résultats de manière lisible
        System.out.println("===== Résultats des skieurs avec abonnements actifs pendant la période =====");
        for (Skier skier : result) {
            printSkierDetails(skier);
        }
        System.out.println("========================================");
    }
    // Méthode utilitaire pour afficher les détails d'un skieur
    private void printSkierDetails(Skier skier) {
        System.out.println("Nom: " + skier.getFirstName() + " " + skier.getLastName());
        System.out.println("Ville: " + skier.getCity());
        System.out.println("Date de naissance: " + skier.getDateOfBirth());
        if (skier.getSubscription() != null) {
            System.out.println("Abonnement: de " + skier.getSubscription().getStartDate() + " à " + skier.getSubscription().getEndDate());
        } else {
            System.out.println("Pas d'abonnement");
        }
        System.out.println("----------------------------------------");
    }
}
