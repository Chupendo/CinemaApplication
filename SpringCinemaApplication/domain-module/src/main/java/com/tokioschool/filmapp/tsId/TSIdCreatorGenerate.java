package com.tokioschool.filmapp.tsId;

import com.github.f4b6a3.tsid.TsidCreator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;

import java.lang.reflect.Member;

/**
 * Clase generadora de identificadores únicos utilizando TSID.
 *
 * Esta clase implementa la interfaz `IdentifierGenerator` de Hibernate
 * para generar identificadores únicos basados en el algoritmo TSID.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class TSIdCreatorGenerate implements IdentifierGenerator {

    /**
     * Constructor de la clase TSIdCreatorGenerate.
     *
     * @param config           configuración de la anotación `@TSId`.
     * @param annotationMember miembro anotado con `@TSId`.
     * @param context          contexto de creación del generador de identificadores.
     */
    public TSIdCreatorGenerate(TSId config, Member annotationMember, CustomIdGeneratorCreationContext context) {
    }

    /**
     * Genera un identificador único.
     *
     * Este método utiliza la clase `TsidCreator` para generar un TSID
     * de 256 bits y lo devuelve como una cadena.
     *
     * @param sharedSessionContractImplementor implementación de la sesión compartida de Hibernate.
     * @param o                                objeto para el cual se genera el identificador.
     * @return un identificador único como cadena.
     */
    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return TsidCreator.getTsid256().toString();
    }
}