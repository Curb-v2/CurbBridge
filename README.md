# Curb Connect
Direct connection between the SmartThings cloud and the Curb cloud.

This integration adds a new power meter in SmartThings for each sensor on your Curb.

## Installation
- Publish both the Curb (Connect) SmartApp and Curb Power Meter DeviceHandler to your account.  Either copy-paste into the IDE or use the GitHub integration.
- Go to the SmartApp settings for the Curb (Connect) SmartApp in the IDE.  Verify that OAuth is enabled.
- Use the SmartThings mobile app to add the Curb (Connect) SmartApp.
- You may need to go to the App Settings on the Curb Connect SmartApp and Click Enable OAuth
- During installation you will see a link "Click to enter Curb credentials", click it and log in with the credentials you use to access your Curb account.
- After logging in, click "Done"
- You will be directed to a page that says "You are connected to Curb".  Click "Done" again.
- You should now see a new device for each of your curb sensors.
- Select your Curb Location to Associate with your Smartthings Location.
- You may not see updates immediately after installation.  Wait five minutes for the ST scheduler to catch up after initial installation.

## Notes
- The default sample rate updates each reading once a minute. You can change the sample rate by clicking on Curb (Connect) in your installed SmartApps and changing the Sample Rate input.
- There is currently a bug which can result in duplicate devices.  If you do end up with duplicate devices, try re-installing the Curb (Connect) SmartApp.  It is less likely to occur if you perform the installation slowly.  Give it ~10 seconds between each step.
