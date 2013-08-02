package com.buschmais.jqassistant.scanner.test;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.buschmais.jqassistant.core.model.api.descriptor.ClassDescriptor;
import com.buschmais.jqassistant.core.model.api.descriptor.PackageDescriptor;
import com.buschmais.jqassistant.scanner.api.ArtifactInformation;
import com.buschmais.jqassistant.scanner.impl.ClassScannerImpl;
import com.buschmais.jqassistant.store.api.Store;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractScannerTest {

    @Mock
    protected Store store;

    protected ClassScannerImpl scanner;

    private final Map<String, PackageDescriptor> packageCache = new HashMap<String, PackageDescriptor>();

    protected ClassDescriptor javaLangObject;
    protected ClassDescriptor _void;

    @Before
    public void createScanner() {
		scanner = new ClassScannerImpl(store, new ArtifactInformation());
        this.javaLangObject = stubClass(Object.class);
        this._void = stubClass("void");
    }

    protected PackageDescriptor stubPackage(String fullQualifiedName) {
        if (fullQualifiedName == null) {
            return null;
        }
        PackageDescriptor packageDescriptor = packageCache.get(fullQualifiedName);
        if (packageDescriptor == null) {
            int i = fullQualifiedName.lastIndexOf('.');
            PackageDescriptor parentDescriptor = null;
            String name;
            if (i != -1) {
                String parentName = fullQualifiedName.substring(0, i);
                name = fullQualifiedName.substring(i + 1, fullQualifiedName.length());
                parentDescriptor = stubPackage(parentName);
            } else {
                name = fullQualifiedName;
            }
            packageDescriptor = new PackageDescriptor();
            packageDescriptor.setFullQualifiedName(fullQualifiedName);
            when(store.createPackageDescriptor(parentDescriptor, name)).thenReturn(packageDescriptor);
            packageCache.put(fullQualifiedName, packageDescriptor);
        }
        return packageDescriptor;
    }

    protected ClassDescriptor stubClass(Class<?> c) {
        return stubClass(stubPackage(c.getPackage().getName()), c.getSimpleName());
    }

    protected ClassDescriptor stubClass(String className) {
        return stubClass((String) null, className);
    }

    protected ClassDescriptor stubClass(String packageName, String className) {
        return stubClass(stubPackage(packageName), className);
    }

    protected ClassDescriptor stubClass(PackageDescriptor packageDescriptor, String className) {
        ClassDescriptor classDescriptor = new ClassDescriptor();
        if (packageDescriptor != null) {
            classDescriptor.setFullQualifiedName(packageDescriptor.getFullQualifiedName() + "." + className);
        } else {
            classDescriptor.setFullQualifiedName(className);
        }
        when(store.createClassDescriptor(packageDescriptor, className)).thenReturn(classDescriptor);
        return classDescriptor;
    }
}
