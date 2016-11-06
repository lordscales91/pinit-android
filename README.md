Pin It! For Android
===
This simple app fetches all images from a given URL, then it allows the user to choose one of them and create a Pin with it.

Download
---
A **pre-release** is available [here](https://github.com/lordscales91/pinit-android/releases/tag/0.1-beta-debug). It's only intended for testing and app review.

License
---
Not defined yet.

Setup for Eclipse
---
If you want to configure this project in Eclipse first you need to set up the required library projects, regardless of whether you configure Maven support or not. Because, the ADT can't handle AARs and will complain of missing resources and classes. If you like, you can run the "compile" goal against the gen-libprojects profile to setup the library projects layout. You will still need to import them to your workspace as android library projects and reference them in the project.

First you will need to clone the repo and add it to the Git Repositories view, then select it from that view and choose "Import Projects..." from the contextual menu. Choose "Use the New Project Wizard" and select the "Android Project from existing code" option.

Go to project properties and add the required library projects (and copy the required jar libraries to the libs folder if you don't plan to use Maven).

Everything should be working fine now. If you want to enable Maven support within Eclipse (highly recommended) just open the contextual menu and choose Configure >  Convert to Maven project.

Questions / Troubleshooting
---

**OH NO! You have leaked your APP ID. Why!?**

**Don't worry** everything is under control(or not). The App ID, called client id in the OAuth jerk is **always leaked** no matter the type of application, this happens because the client id is passed as a GET parameter in the URL so any skilled enough user can retrieve it. It's just another [OAuth flaw](http://www.oauthsecurity.com/)

**But... This means that a malicious hacker could create a fake app and use the stealed app id. Isn't it?**

Sadly yes, and there isn't any good workaround for it. Even if I remove the value from the manifest it's easily obtainable as I stated before. So, the only thing we can do is pray that users don't download the app from untrusted sources.

**Can I enable Maven support and still use "Run as Android Application"(Eclipse)?**

I'm afraid it's not possible. The reason is that there is some kind of interference between the Maven plugin (either m2e or m2e-android) and the Android Library Update process from the ADT tools. This issue causes the libs from referenced projects to be excluded from the dex tool. Therefore, while the APK will build, it will not be executed properly. You can run the "verify" goal to produce the sama result.

**Can I set up this project in Android Studio?**

Probably yes, provided it has support for Maven and blindly obeys POM's defined project layout there should no problem. However, I have not tested it personally and if I can, I will stay away from Android Studio **forever**.

About
---
I started this as a personal project, just to fulfill my own need: "a way to create pins from my smartphone". I have the official Pinterest app installed and when it comes to create pins from a URL it fails miserably. Sometimes it doesn't lets you choose the image you want to pin, and when it does the pin process fails in most cases (from my experience).

Since I think that there could be other users with similar issues I decided to publish it. By the time you read this probably I have already submitted my app to Pinterest through their developer's platform. (Let's pray it gets approved because the above statements don't help...).

After deciding to share this piece of software I discovered the awesome [Android Maven Plugin](http://simpligility.github.io/android-maven-plugin/) and its corresponding support in Eclipse via the [m2e-android](http://rgladwell.github.io/m2e-android/) plugin. I couldn't resist to try it, specially after my frustration trying to use Gradle.

It took me a few days but finally I managed to make it work. The best part is that this is probably the first hybrid project that is compatible with both the old ADT standard and Maven.

Finally I would like to mention all the projects that made this one possible.

* Eclipse IDE
* Android Development Tools (ADT)
* Maven plugins for Eclipse
* The Android Maven Plugin
* The Ant Build system. Because it's still useful through Maven antrun plugin and it's also its ancestor. 
