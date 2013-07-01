package org.geoserver.community.css.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;

class CssSubmitButton extends AjaxButton {
    private final String id;
    private final Form<?> styleEditor;
    private final CssDemoPage page;
    private final String cssSource;
    private final PropertyModel<String> styleBody;

    public CssSubmitButton(
        final String id,
        final Form<?> styleEditor,
        final CssDemoPage page,
        final String cssSource,
        final PropertyModel<String> styleBody)
    {
        super("submit", styleEditor);
        this.id = id;
        this.styleEditor = styleEditor;
        this.page = page;
        this.cssSource = cssSource;
        this.styleBody = styleBody;
    }

    @Override
    public void onSubmit(AjaxRequestTarget target, Form<?> form) {
        try {
            File file = page.findStyleFile(cssSource);
            String sld = page.cssText2sldText(styleBody.getObject());
            Writer writer = new FileWriter(file);
            writer.write(styleBody.getObject());
            writer.close();
            page.catalog().getResourcePool().writeStyle(
              page.getStyleInfo(), new ByteArrayInputStream(sld.getBytes()));
            page.catalog().save(page.getStyleInfo());
            if (page.sldPreview != null) target.addComponent(page.sldPreview);
            if (page.map != null) target.appendJavascript(page.map.getUpdateCommand());
        } catch (Exception e) {
            throw new WicketRuntimeException(e);
        }

    }
}
