package com.seventh7.mybatis.generate;

import com.google.common.primitives.Ints;

import java.util.Collection;
import java.util.Objects;

/**
 * @author yanglin
 */
public abstract class GenerateModel {

    public static final GenerateModel START_WITH_MODEL = new StartWithModel();

    private static final GenerateModel END_WITH_MODEL = new EndWithModel();

    private static final GenerateModel CONTAIN_MODEL = new ContainModel();

    public static GenerateModel getInstanceOrDefault(String identifier) {
        Integer ident = Ints.tryParse(identifier);
        return Objects.nonNull(ident) ?
                getInstance(ident) :
                START_WITH_MODEL;
    }

    public static GenerateModel getInstance(int identifier) {
        switch (identifier) {
            case 0:
                return START_WITH_MODEL;
            case 1:
                return END_WITH_MODEL;
            case 2:
                return CONTAIN_MODEL;
            default:
                throw new AssertionError();
        }
    }

    private boolean matchesAny(String[] patterns, String target) {
        for (String pattern : patterns) {
            if (apply(pattern, target)) {
                return true;
            }
        }
        return false;
    }

    boolean matchesAny(Collection<String> patterns, String target) {
        return matchesAny(patterns.toArray(new String[patterns.size()]), target);
    }

    protected abstract boolean apply(String pattern, String target);

    public abstract int getIdentifier();

    private static class StartWithModel extends GenerateModel {

        @Override
        protected boolean apply(String pattern, String target) {
            return target.startsWith(pattern);
        }

        @Override
        public int getIdentifier() {
            return 0;
        }
    }

    private static class EndWithModel extends GenerateModel {

        @Override
        protected boolean apply(String pattern, String target) {
            return target.endsWith(pattern);
        }

        @Override
        public int getIdentifier() {
            return 1;
        }
    }

    private static class ContainModel extends GenerateModel {

        @Override
        protected boolean apply(String pattern, String target) {
            return target.contains(pattern);
        }

        @Override
        public int getIdentifier() {
            return 2;
        }
    }
}
