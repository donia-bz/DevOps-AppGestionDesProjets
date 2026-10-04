package tn.esprit.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.service.impl.EntrepriseServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntrepriseServiceImplTest {

    @Mock
    private EntrepriseRepository entrepriseRepository;

    @InjectMocks
    private EntrepriseServiceImpl entrepriseService;

    private Entreprise creerEntreprise(Long id, String nom, String adresse) {
        Entreprise e = new Entreprise();
        e.setId(id);
        e.setNom(nom);
        e.setAdresse(adresse);
        return e;
    }

    @Test
    void addEntreprise_doitEnregistrerEtRetournerLEntreprise() {
        Entreprise e = creerEntreprise(1L, "Esprit", "Tunis");
        when(entrepriseRepository.save(e)).thenReturn(e);

        Entreprise resultat = entrepriseService.addEntreprise(e);

        assertNotNull(resultat);
        assertEquals("Esprit", resultat.getNom());
        verify(entrepriseRepository, times(1)).save(e);
    }

    @Test
    void getEntrepriseById_quandExiste_doitRetournerLEntreprise() {
        Entreprise e = creerEntreprise(1L, "Esprit", "Tunis");
        when(entrepriseRepository.findById(1L)).thenReturn(Optional.of(e));

        Entreprise resultat = entrepriseService.getEntrepriseById(1L);

        assertNotNull(resultat);
        assertEquals("Tunis", resultat.getAdresse());
    }

    @Test
    void getEntrepriseById_quandInexistante_doitRetournerNull() {
        when(entrepriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(entrepriseService.getEntrepriseById(99L));
    }

    @Test
    void getAllEntreprises_doitRetournerToutesLesEntreprises() {
        List<Entreprise> liste = Arrays.asList(
                creerEntreprise(1L, "Esprit", "Tunis"),
                creerEntreprise(2L, "Vermeg", "Lac 2"));
        when(entrepriseRepository.findAll()).thenReturn(liste);

        List<Entreprise> resultat = entrepriseService.getAllEntreprises();

        assertEquals(2, resultat.size());
        verify(entrepriseRepository).findAll();
    }

    @Test
    void deleteEntreprise_doitAppelerLeRepository() {
        entrepriseService.deleteEntreprise(1L);

        verify(entrepriseRepository, times(1)).deleteById(1L);
    }
}
