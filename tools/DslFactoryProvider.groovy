package tools;

import javaposse.jobdsl.dsl.DslFactory

public class DslFactoryProvider {

    private static DslFactory _dslFactory;

    static public def init(DslFactory dslFactory) {
        _dslFactory = dslFactory;

    }

    static public def DslFactory getInstance() {
        return _dslFactory;
    };

}