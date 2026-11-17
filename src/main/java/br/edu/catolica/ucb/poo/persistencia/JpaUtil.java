package br.edu.catolica.ucb.poo.persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Mantém uma única EntityManagerFactory para toda a aplicação.
 */
public final class JpaUtil {

    private static final EntityManagerFactory FABRICA =
            Persistence.createEntityManagerFactory("condominioPU");

    private JpaUtil() {
    }

    public static EntityManager criarEntityManager() {
        return FABRICA.createEntityManager();
    }

    public static void fechar() {
        FABRICA.close();
    }
}
