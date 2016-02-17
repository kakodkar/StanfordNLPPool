package com.learningdisaster;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by michael_kelso
 */

public class NLPService extends BasePooledObjectFactory<StanfordNLP> {
    public static GenericObjectPool<StanfordNLP> stanfordNLP = new GenericObjectPool<StanfordNLP>(new NLPService()
            , new GenericObjectPoolConfig() {{
        setMaxIdle(1);
        setMaxTotal(-1);

    }});

    public static StanfordNLP getStanfordNLP() throws Exception {
        return stanfordNLP.borrowObject();
    }

    public static void returnStanfordNLP(StanfordNLP obj) throws Exception {
        stanfordNLP.returnObject(obj);
    }


    @Override
    public StanfordNLP create() throws Exception {
        synchronized (NLPService.class) {
            return new StanfordNLP();
        }
    }

    @Override
    public PooledObject<StanfordNLP> wrap(StanfordNLP obj) {
        return new DefaultPooledObject<>(obj);
    }

}
