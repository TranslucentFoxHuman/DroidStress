# DroidStress

## What is this?

This program is a simple stress test for Android devices.

It offers the following features:

- Forces the CPU to perform unnecessary calculations to generate load  
- Executes the process with a specified number of threads  
- Runs in the background, allowing work to continue even when the app is closed  
- Displays a solid black blank screen to prevent burn-in the display for devices that has OLED display  

This app was created after I discovered there were no stress‑test applications available on F‑Droid.

## How to Use

On Android 13 and newer, When this app started at the first time, it will show a dialog requesting notification permission. This is required to the app can continue processing even when its window is not open.

In the **Threads:** input field, enter the number of threads to use.

Press **Start** to begin processing. Once started, a notification will appear indicating that background work is in progress. Press **Stop** to halt processing.

Press **Show blank screen** to display a solid black screen and temporarily stop the screen from sleeping. This is useful when running long stress tests on OLED displays.

Click **About...** in the menu bar to view the app's version and license information.

## Use Cases

- **Battery refresh**  
  If the battery level is not displayed correctly, fully charging to 100 % and then discharging to 0 % can recalibrate the measurement. This app helps consume battery during that process.

- **Warming the phone**  
  A device that has cooled down can suddenly shut down or freeze, making it unstable. Use this program in the winter to warm the device and stabilize its operation before it becomes unstable.

- **Staying warm with the device**  
  Do you have a device with the CPU such as the Snapdragon 810? It's perfect for keeping warm! Use this app on the device and keep it in your pocket to warm your hands.

## Your Help is Needed!

I developed this project on my own, so there are features that are difficult for me to implement:

- **A better app icon**  
  The icon must be GPL‑compatible, editable in Inkscape as an SVG file. Please do not use AI to create it - Everyone can easily give instructions to AI to generate a logo.

- **A CPU‑intensive calculation algorithm**  
  Currently the program uses a very simple calculation to load the CPU. I need an algorithm that can generate a stronger load. The algorithm should be implementable in Kotlin, Java, C, etc., work the same in all languages, not depend on external libraries, and not be tied to a particular architecture.

## License

This software is provided under the GNU General Public License Version 3 (or later).
