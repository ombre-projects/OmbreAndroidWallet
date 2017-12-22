# SumoWallet
An Android Sumokoin Wallet

### Quickstart
- Download the APK for the most current release [here](https://github.com/SadBatman/sumowallet/releases) and install it
- Run the App and select "Generate Wallet" to create a new wallet or recover a wallet
- Advanced users can copy over synced wallet files (all files) onto sdcard in directory Monerujo (created first time App is started)
- See the [FAQ](doc/FAQ.md)

### Disclaimer
You may lose all your Moneroj if you use this App. Use at own risk ;-)

### Random Notes
- Uses Sumokoin v0.2.0
- Based on the awesome xmrwallet
- Currently only android32 (runs on 64-bit as well)
- Syncing is slow
- Use your own daemon - it's easy
- Screen stays on until first sync is complete
- Saves wallet only on first sync and when sending transactions or editing notes

### TODO
 - Add SSL support to the next Sumokoin release
 - Update the UI to fit the Sumokoin theme

### Issues / Pitfalls
- The backups folder is now called "backups" and not ".backups" - which in most file explorers was a hidden folder
- Wallets are now created directly in the "monerujo" folder, and not in the ".new" folder as before
- You may want to check the old folders with a file browsing app and delete the ".new" and ".backups" folders AFTER moving neccessary wallet files to the new locations. Or simply make new backups from within Monerujo.
- Also note, that on some devices the backups will only be visible on a PC over USB after a reboot of the device (it's an Android bug/feature)
- Created wallets on a private testnet are unusable because the restore height is set to that
of the "real" testnet.  After creating a new wallet, make a **new** one by recovering from the seed.
The official monero client shows the same behaviour.

### HOW TO BUILD
If you want to build them yourself (recommended) check out [the instructions](doc/BUILDING-external-libs.md)

Then, fire up Android Studio and build the APK.
