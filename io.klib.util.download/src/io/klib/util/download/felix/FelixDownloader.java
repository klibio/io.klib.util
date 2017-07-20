package io.klib.util.download.felix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component
public class FelixDownloader {

	private static final String DL_URL_FELIX_OFFICIAL = "http://felix.apache.org/downloads.cgi";
	private static final String DL_URL_FELIX_FHTE = "http://ftp-stud.hs-esslingen.de/pub/Mirrors/ftp.apache.org/dist//felix/";
	private final static String USERDIR = System.getProperty("user.dir");

	@Activate
	public void activate() {

		URL url;
		try {
			url = new URL(DL_URL_FELIX_OFFICIAL);
			String dlURLasFilenam = URLEncoder.encode(url.toString(), "UTF-8");
			String dlDir = USERDIR + File.separator + "_downloadDir" + File.separator + dlURLasFilenam;
			new File(dlDir).mkdirs();
			try (BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream()))) {
				String content = buffer.lines().collect(Collectors.joining("\n"));
				System.out.println(content);
				Files.write(Paths.get(dlDir, "_" + dlURLasFilenam + ".md"), content.getBytes(Charset.forName("UTF-8")),
						StandardOpenOption.CREATE, StandardOpenOption.WRITE);

				Pattern p = Pattern.compile(".*<a href=\"(.*\\.jar)\".*");
				Matcher m = p.matcher(content);

				while (m.find()) {
					String downloadURL = m.group(1);
					if (!downloadURL.startsWith("http://")) {
						downloadURL = DL_URL_FELIX_FHTE + "/" + downloadURL;
					}
					String filename = downloadURL.substring(downloadURL.lastIndexOf("/") + 1, downloadURL.length());
					System.out.format("downloading ... %s\n", downloadURL);

					try {
						URL fileUrl = new URL(downloadURL);
						ReadableByteChannel rbc = Channels.newChannel(fileUrl.openStream());
						FileOutputStream fos = new FileOutputStream(new File(dlDir, filename));
						fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
						fos.close();
						rbc.close();
					} catch (FileNotFoundException e) {
						String message = String.format("File not found %s", e.getMessage());
						Files.write(Paths.get(dlDir, "_errors.md"), message.getBytes(), StandardOpenOption.CREATE,
								StandardOpenOption.APPEND);
						System.err.println(message);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("done.");
	}
}
