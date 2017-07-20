package io.klib.util.download.pokedex;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Scanner;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component
public class PokemonAbsorber {

    private static final String PokedexURL   = "http://www.pokemon.com/us/pokedex/";
    // private static String ImageRootURL = "http://assets.pokemon.com//assets/cms2/img/pokedex/detail/";
    private static String       ImageRootURL = "http://files.pokefans.net/images/pokemon-go/modelle/";
    private static File         localDir     = new File("c:" + File.separator + "tmp" + File.separator + "pokemon");

    @Activate
    public void activate() {
        Instant now = Instant.now();
        Date from = Time.from(now);
        System.out.println(String.format("# started yyyyMMdd-HHmmss", from));
        downloadImages();
        System.out.println(String.format("# execution took %s ms", ChronoUnit.MILLIS.between(now, Instant.now())));
    }

    public void parse() {
        URL root = null;
        try {
            root = new URL(PokedexURL);
            Scanner scanner = new Scanner(root.openStream(), "UTF-8");
            scanner.useDelimiter("\\Z");
            String content = scanner.next();
            scanner.close();
            TagNode htmlNode = new HtmlCleaner().clean(content);
            org.w3c.dom.Document doc = new DomSerializer(new CleanerProperties()).createDOM(htmlNode);
            XPath xpath = XPathFactory.newInstance().newXPath();
            String xpathQuery = "//a[contains (@href, '/us/pokedex/') ]";
            String str = (String) xpath.evaluate(xpathQuery, doc, XPathConstants.STRING);
            System.out.println("" + str);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadImages() {
        localDir.mkdirs();
        for (int i = 0; i < 300; i++) {
            String filename = String.format("%03d", i) + ".png";
            try {
                URL website = new URL(ImageRootURL + filename);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                System.out.println("downloading ... " + website);
                FileOutputStream fos = new FileOutputStream(Paths.get(localDir.getAbsolutePath(), filename).toFile());
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
