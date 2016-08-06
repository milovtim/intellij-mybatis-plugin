package com.seventh7.mybatis.alias;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.intellij.ide.actions.ImportSettingsActionKt;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.seventh7.mybatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yanglin
 */
public class InnerAliasResolver extends AliasResolver {

    //TODO move default aliases to file
    private final Set<AliasDesc> innerAliasDescs;

    @SuppressWarnings("ConstantConditions")
    public InnerAliasResolver(Project project) {
        super(project);
        final ImmutableMap<String, String> typesAliasMap = ImmutableMap.<String, String>builder()
                .put("string", "java.lang.String")
                .put("byte", "java.lang.Byte")
                .put("long", "java.lang.Long")
                .put("short", "java.lang.Short")
                .put("int", "java.lang.Integer")
                .put("integer", "java.lang.Integer")
                .put("double", "java.lang.Double")
                .put("float", "java.lang.Float")
                .put("boolean", "java.lang.Boolean")
                .put("date", "java.util.Date")
                .put("decimal", "java.math.BigDecimal")
                .put("object", "java.lang.Object")
                .put("map", "java.util.Map")
                .put("hashmap", "java.util.HashMap")
                .put("list", "java.util.List")
                .put("arraylist", "java.util.ArrayList")
                .put("collection", "java.util.Collection")
                .put("iterator", "java.util.Iterator")
                .build();

        //noinspection OptionalGetWithoutIsPresent Map is staticaly defined
        if (!JavaUtils.findClazz(project, typesAliasMap.values().stream().findFirst().get()).isPresent()) {
            //if no JVM type class provided --> no jdk specified in mybatis project
            Notifications.Bus.notify(new Notification("Mybatis Plugin", "No jdk provided", "Please, specify jdk",
                    NotificationType.ERROR));
        }

        innerAliasDescs = typesAliasMap.entrySet().stream()
                .map(entry -> AliasDesc.create(JavaUtils.findClazzOrNull(project, entry.getValue()), entry.getKey()))
                .collect(Collectors.toSet());
    }

    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        return innerAliasDescs;
    }

}
