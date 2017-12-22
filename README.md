# SumoWallet
An Android Sumokoin Wallet

### Quickstart
- Download the APK for the most current release [here](https://github.com/SadBatman/sumowallet/releases) and install it
- Run the App and select "Generate Wallet" to create a new wallet or recover a wallet
- Advanced users can copy over synced wallet files (all files) onto sdcard in directory Monerujo (created first time App is started)
- See the [FAQ](doc/FAQ.md)

### Disclaimer
You may lose all your Sumo if you use this App. Use at own risk ;-)

### Random Notes
- Uses Sumokoin v0.2.0
- Based on the awesome xmrwallet
- Currently only android32 (runs on 64-bit as well)
- Syncing is slow
- Use your own daemon - it's easy
- Screen stays on until first sync is complete
- Saves wallet only on first sync and when sending transactions or editing notes
- Wallets are created directly in the "sumo_wallet" folder.

### TODO
 - Add SSL support to the next Sumokoin release
 - Update the UI to fit the Sumokoin theme

### HOW TO BUILD
If you want to build them yourself (recommended) check out [the instructions](doc/BUILDING-external-libs.md)

Then, fire up Android Studio and build the APK.
