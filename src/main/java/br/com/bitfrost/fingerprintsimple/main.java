package br.com.bitfrost.fingerprintsimple;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

public class main {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//Capture Runtime 
		Runtime run = Runtime.getRuntime();
			
		//Exec the command to capture first fingerprint image 1 
		boolean read = false;
		int countTime = 0;
		run.exec("python3 capture.py finger1.png");
		do {
			if(new File("finger1.png").isFile()) {
				read = true;
				System.out.println("Leitura 1 confirmada. ");
			}else {
				new Thread();
				Thread.sleep(1000);
				countTime++;
				System.out.println("Aguardando leitura 1 - " + countTime +"segs... ");
				if(countTime > 20) {
				System.out.println("Saindo da execução por falta de confirmação. ");
				read = true;
				run.exec("pkill -f capture.py ");
				}
			}	
		}while(read == false);
		
		//Exec the command to capture fingerprint image 2
		read = false;
		countTime = 0;		
		new Thread();
		Thread.sleep(2000);
		run.exec("python3 capture.py finger2.png");
		do {
			if(new File("finger2.png").isFile()) {
				read = true;
				System.out.println("Leitura 2 confirmada. ");
			}else {
				new Thread();
				Thread.sleep(1000);
				countTime++;
				System.out.println("Aguardando leitura 2 - " + countTime +"segs... ");
				if(countTime > 20) {
				System.out.println("Saindo da execução por falta de confirmação. ");
				read = true;
				run.exec("pkill -f capture.py ");
				}
			}			
		}while(read == false);
		
		//Compare first fingerprint 1 and image 2
		read = false;
		countTime = 0;
		do {
			if(new File("finger1.png").isFile() && new File("finger2.png").isFile()) {
				try {
					byte[] probeImage = Files.readAllBytes(Paths.get("finger1.png"));
					System.out.println("Byte capturado: " + probeImage); //Don't delete this
					String encodedStringFromProbeImage = Base64.getEncoder().encodeToString(probeImage); // Encode Byte to Base64 to save to the database  
					System.out.println("String codificada para o servidor é: " + encodedStringFromProbeImage);  
					byte[] decodedBytesFromProbeImage = Base64.getDecoder().decode(encodedStringFromProbeImage); // Decode the data saved in the database to Byte again
					
					byte[] candidateImage = Files.readAllBytes(Paths.get("finger2.png")); //Read second fingerprint to Byte
					
					FingerprintTemplate probe = new FingerprintTemplate(// Convert to FingerprintTemplate
					    new FingerprintImage()
					        .dpi(500)
					        .decode(decodedBytesFromProbeImage));

					FingerprintTemplate candidate = new FingerprintTemplate(// Convert to FingerprintTemplate
					    new FingerprintImage()
					        .dpi(500)
					        .decode(candidateImage));
					
					double score = new FingerprintMatcher()// Analise 
						    .index(probe)
						    .match(candidate);
					
					double threshold = 40;
					boolean matches = score >= threshold; //result
					
					System.out.println("Resultado da comparação é: " + matches);
					read = true;
					
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Erro ao realizar a comparação " + e);
				}
			}else {
				new Thread();
				Thread.sleep(1000);
				countTime++;
				System.out.println("Aguardando leituras 1 e 2... ");
				if(countTime >= 0) {
				System.out.println("Saindo da execução por falta de confirmação. ");
				read = true;
				System.out.println("Erro ao comparar. ");
				}
			}
			
		}while(read == false);
		

		
		Files.deleteIfExists(Paths.get("finger1.png" )); 
		Files.deleteIfExists(Paths.get("finger2.png" )); 
	}
}
