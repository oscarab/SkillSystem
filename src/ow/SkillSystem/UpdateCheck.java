package ow.SkillSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
 
import javax.net.ssl.X509TrustManager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class UpdateCheck {
	
	private InputStream input;
	
	public UpdateCheck() {
		try {
		SSLContext sslcontext = SSLContext.getInstance("SSL","SunJSSE");  
        sslcontext.init(null, new TrustManager[]{new MyX509TrustManager()}, new java.security.SecureRandom());  
		URL url = new URL("https://www.luogu.com.cn/blog/oscarwen/post-test");
        HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {  
            public boolean verify(String s, SSLSession sslsession) {  
                System.out.println("WARNING: Hostname is not matched for cert.");  
                return true;  
            }  
        };  
        HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);  
        HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());  
        
		HttpsURLConnection httpsConn = (HttpsURLConnection)url.openConnection();
		input = httpsConn.getInputStream();
		}catch (IOException | KeyManagementException | NoSuchAlgorithmException | NoSuchProviderException e) {
				e.printStackTrace();
				Bukkit.getConsoleSender().sendMessage("出现错误！无法获取最新版本信息！");
		}
	}
	
	//获取最新版本号并对比
	public boolean isNewVersion(String version) {
		try {
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			while(reader.ready()) {
				String line = reader.readLine();
				
				if(!line.contains("version"))
					continue;
				
				if(line.contains(version)) {
					return true;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage("出现错误！无法获取最新版本信息！");
			return true;
		}
		return false;
	}
	
	public void check(CommandSender sender) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

			public void run() {
				sender.sendMessage("§a============= §9SkillSystem > §c更新检查§a =============");
				if(isNewVersion(Main.version)) {
					sender.sendMessage("§9 > §a当前已经是最新版本！");
				}else {
					sender.sendMessage("§9 > §b发现新版本，请前往以下网址更新！");
					sender.sendMessage("§9 > §bhttps://www.mcbbs.net/thread-887197-1-1.html <");
				}
				sender.sendMessage("§a============= §9SkillSystem > §c检查完成§a =============");
				
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	

public class MyX509TrustManager implements X509TrustManager {
	public void checkClientTrusted(X509Certificate[] chain, String authType)
		    throws CertificateException
		  {
		  }
 
		  public void checkServerTrusted(X509Certificate[] chain, String authType)
		    throws CertificateException
		  {
		  }
 
		  public X509Certificate[] getAcceptedIssuers()
		  {
		    return null;
		  }

}
}
