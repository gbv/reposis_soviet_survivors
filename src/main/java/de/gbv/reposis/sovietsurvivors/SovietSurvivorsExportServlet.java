package de.gbv.reposis.sovietsurvivors;

import jakarta.servlet.ServletOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.mycore.datamodel.metadata.MCRMetaEnrichedLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.niofs.MCRPath;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.services.zipper.MCRZipServlet;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;

public class SovietSurvivorsExportServlet extends MCRZipServlet {

    @Override
    protected void render(MCRServletJob job, Exception ex) throws Exception {
        if (ex != null) {
            throw ex;
        }
        if (job.getResponse().isCommitted()) {
            return;
        }
        MCRObjectID id = (MCRObjectID) job.getRequest().getAttribute(KEY_OBJECT_ID);
        try (ServletOutputStream sout = job.getResponse().getOutputStream()) {
            StringBuffer requestURL = job.getRequest().getRequestURL();
            if (job.getRequest().getQueryString() != null) {
                requestURL.append('?').append(job.getRequest().getQueryString());
            }
            try (ZipArchiveOutputStream container = createContainer(sout, id.toString())) {
                job.getResponse().setContentType(getMimeType());
                String filename = id + ".zip";
                job.getResponse().addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

                if (!id.getTypeId().equals("mods")) {
                    throw new IllegalArgumentException("Only MODS objects can be downloaded");
                }

                MCRObject object = MCRMetadataManager.retrieveMCRObject(id);
                MCRMetaEnrichedLinkID derivateLink = object.getStructure().getDerivates().stream().findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No derivate found"));

                Path path = MCRPath.getPath(derivateLink.getXLinkHref(), "/");
                Files.walkFileTree(path, new CompressVisitor(this, container));

                disposeContainer(container);
            }
        }
    }


    private static class CompressVisitor extends SimpleFileVisitor<Path> {
        private SovietSurvivorsExportServlet impl;
        private ZipArchiveOutputStream container;

        CompressVisitor(SovietSurvivorsExportServlet impl, ZipArchiveOutputStream container) {
            this.impl = impl;
            this.container = container;
        }

        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            this.impl.sendCompressedDirectory(MCRPath.toMCRPath(dir), attrs, this.container);
            return super.preVisitDirectory(dir, attrs);
        }


        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String lowerCaseName = file.toString().toLowerCase(Locale.ROOT);
            if(lowerCaseName.endsWith(".xml") || lowerCaseName.endsWith(".pdf")) {
                this.impl.sendCompressedFile(MCRPath.toMCRPath(file), attrs, this.container);
            }

            return super.visitFile(file, attrs);
        }
    }
}

