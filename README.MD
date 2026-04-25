# DroidStress

## What is this?

This program is a simple stress test for Android devices.

It offers the following features:

- Forces the CPU to perform unnecessary calculations to generate load  
- Executes the process with a specified number of threads  
- Runs in the background, allowing work to continue even when the app is closed  
- Displays a solid black blank screen to prevent burn-in the display for devices that has OLED display  
- Also perform the memory stress test

This app was created after I discovered there were no stress‑test applications available on F‑Droid.

## How to Use

On Android 13 and newer, When this app started at the first time, it will show a dialog requesting notification permission. This is required to the app can continue processing even when its window is not open.

In the **"Threads:"** input field, enter the number of threads to use.

In the **"Duration (minute):"** input field, enter the amount of time, in minutes, that the process should run.  
If a number is entered, the process will run for that many minutes.  
To run the process indefinitely, leave the field blank.

In the **"Memory stress test (MiB:)"** input field, enter a value in MiB if you want to perform a memory stress test.  
The test repeatedly allocates the specified amount of memory, writes arbitrary data to it, and then frees it, thereby stressing the memory.

Press **"Start"** to begin processing. Once started, a notification will appear indicating that background work is in progress. Press **"Stop"** to halt processing.

Press **"Show blank screen"** to display a solid black screen and temporarily stop the screen from sleeping. This is useful when running long stress tests on OLED displays.

Click **A"bout..."** in the menu bar to view the app's version and license information.

### Memory Stress Test  
Starting with v1.6.0, a memory‑stress‑test feature was added.  
The value entered in the **"Memory stress test (MiB):"** field determines how much memory to allocate; the program writes arbitrary data to the allocated region and then frees it repeatedly, creating a memory load.  
Because memory I/O waits occur, the CPU load may be relatively low.

The ideal value for the **"Memory stress test (MiB):"** field is between **1 – 64**.  
Values larger than this may fail to allocate memory properly, or the allocation wait times may increase, resulting in reduced CPU load.

Android imposes a limit on how much memory each app can use—typically **512 MiB**.  
Even if the system has more free memory, you cannot allocate memory beyond this limit. Therefore, if you input a value larger than the available memory under this limit, the memory size actually used for the stress test will be capped at that limit.

Specifying a large memory size may trigger a toast notification saying **“Memory is full!”**.  
This appears when the memory‑stress test cannot allocate the requested amount of memory. It may also appear even when the requested size is smaller than the Android OS limit.  
This behavior is due to the way memory is managed in Kotlin/Java. Unlike languages such as C, where the programmer can free memory at arbitrary times, unused memory in Kotlin/Java is not freed immediately; the OS (VM) automatically releases it based on resource usage. Consequently, the memory allocated in the previous cycle may still be present, and attempting to allocate additional memory in this state can fail even if the requested size is below the limit.

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
For more information, see the COPYING file or [https://gnu.org/licenses](https://gnu.org/licenses)
