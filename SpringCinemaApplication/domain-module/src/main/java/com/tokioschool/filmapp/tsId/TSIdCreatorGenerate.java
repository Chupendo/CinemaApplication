package com.tokioschool.filmapp.tsId;

import com.github.f4b6a3.tsid.TsidCreator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;

import java.lang.reflect.Member;

public class TSIdCreatorGenerate implements IdentifierGenerator {

    public TSIdCreatorGenerate(TSId config, Member annotationMember, CustomIdGeneratorCreationContext context) {
    }

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return TsidCreator.getTsid256().toString();
    }
}
