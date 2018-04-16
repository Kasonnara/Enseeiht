package Entities;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FichierAdditionnel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    int id;

    // Pas encore Implémenté
}
