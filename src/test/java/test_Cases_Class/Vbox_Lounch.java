package test_Cases_Class;

import org.testng.annotations.Test;
import java.io.IOException;

import base_Classes.Base_Page;
import testCasesCode.RemoteVbox;

public class Vbox_Lounch extends Base_Page {

    @Test
    public void vmStartInstallAgent() throws InterruptedException, IOException {
        // RemoteVbox class ka object banate hain
        RemoteVbox remoteVbox = new RemoteVbox(driver.get());

        // Installer path pass karte hain (double quotes mein)
        String installerPath = "\"D:\\DFCloud\\Downloads\\FWAWebInstaller_Faronics Default.exe\"";

        // ✅ Step 1: Rename installer (method internally should rename or log path)
        remoteVbox.renameInstaller(installerPath);

        // ✅ Step 2: Start VirtualBox VM on remote system using PsExec
        remoteVbox.StartVM_RemotelyUsingPsExec();

        // ✅ Step 3: Aap yahan additional steps bhi add kar sakte ho jaise agent install, status check etc.
        System.out.println("✅ VM launch test completed.");
    }
}
