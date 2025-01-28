<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:mods="http://www.loc.gov/mods/v3"
        xmlns:mcrxsl="xalan://org.mycore.common.xml.MCRXMLFunctions"
        xmlns:xlink="http://www.w3.org/1999/xlink"
        exclude-result-prefixes="mods mcrxsl xlink"
>
    <xsl:import href="xslImport:solr-document:sovietsurvivors-solr.xsl" />

    <xsl:template match="mycoreobject[contains(@ID,'_mods_')]">
        <xsl:apply-imports />
        <!-- fields from mycore-mods -->
        <xsl:apply-templates select="metadata/def.modsContainer/modsContainer/mods:mods/mods:language/mods:languageTerm[@authority='rfc5646' and @type='code']" mode="survivors" />
    </xsl:template>


    <xsl:template match="mods:languageTerm" mode="survivors">
        <field name="survivors.mods.language">
            <xsl:value-of select="." />
        </field>
    </xsl:template>

</xsl:stylesheet>