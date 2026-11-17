package br.edu.catolica.ucb.poo.persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * DAO genérico: o mesmo código de CRUD serve para todas as entidades.
 */
public class Repositorio<T> {

    private final Class<T> classe;

    public Repositorio(Class<T> classe) {
        this.classe = classe;
    }

    public T salvar(T entidade) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entidade);
            em.getTransaction().commit();
            return entidade;
        } catch (RuntimeException e) {
            desfazer(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public T atualizar(T entidade) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            T atualizada = em.merge(entidade);
            em.getTransaction().commit();
            return atualizada;
        } catch (RuntimeException e) {
            desfazer(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<T> buscarPorId(Long id) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return Optional.ofNullable(em.find(classe, id));
        } finally {
            em.close();
        }
    }

    public List<T> listarTodos() {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery("select e from " + classe.getSimpleName() + " e order by e.id", classe)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean remover(Long id) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            T entidade = em.find(classe, id);
            if (entidade == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(entidade);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            desfazer(em);
            throw e;
        } finally {
            em.close();
        }
    }

    /** Consulta JPQL com parâmetros posicionais (?1, ?2, ...). */
    public <R> List<R> consultar(String jpql, Class<R> tipo, Object... parametros) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            TypedQuery<R> consulta = em.createQuery(jpql, tipo);
            for (int i = 0; i < parametros.length; i++) {
                consulta.setParameter(i + 1, parametros[i]);
            }
            return consulta.getResultList();
        } finally {
            em.close();
        }
    }

    private void desfazer(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
