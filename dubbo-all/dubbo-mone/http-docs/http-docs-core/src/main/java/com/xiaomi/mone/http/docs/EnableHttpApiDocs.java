package com.xiaomi.mone.http.docs;

import com.xiaomi.mone.http.docs.core.HttpApiDocsAnnotationScanner;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enable http api doc.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
@Import({HttpApiDocsAnnotationScanner.class})
public @interface EnableHttpApiDocs {
}
