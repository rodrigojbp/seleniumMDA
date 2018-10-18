package br.gov.mda.sead;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class ColetarMunicipios {

	public static void main(String[] args) {

		BufferedWriter writer = null;
		try {

			System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
			WebDriver driver = new FirefoxDriver();

			String baseUrl = "http://smap14.mda.gov.br/extratodap/PesquisarDAP";
			driver.get(baseUrl);
			driver.manage().window().maximize();

			driver.findElement(By.xpath("//button[@class='form-control btn btn-success']")).click();
			driver.findElement(By.xpath("//a[@href='#Municipio']")).click();

			Thread.sleep(2000);

			Util.UF[] ufs = Util.UF.values();
			for (int i = 0; i < ufs.length; i++) {
				driver.findElement(By.xpath("//option[@value='" + ufs[i].getValor() + "']")).click();
				Thread.sleep(3000);

				File arquivoMunicipio = new File("ufs" + "\\" + ufs[i].toString() + ".txt");
				arquivoMunicipio.getParentFile().mkdirs();
				writer = new BufferedWriter(new FileWriter(arquivoMunicipio));

				String municipios = driver.findElement(By.xpath("//select[@id='ddlMunicipio']")).getText();
				writer.write(municipios);
				writer.close();
			}

			driver.close();
			System.out.println("Coleta dos municípios realizada com sucesso!");
		} catch (Exception e) {
			System.err.println("Erro na coleta de dados!");
			e.printStackTrace();
		} finally {

		}

	}
}
