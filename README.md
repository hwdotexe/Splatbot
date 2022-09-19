[![Build](https://github.com/hwdotexe/Splatbot/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/hwdotexe/Splatbot/actions/workflows/maven.yml)

# Woomy!
Hi, I'm Splatbot! Your personal Splatoon 3 expert!

## What is Splatbot?
Splatbot is a simple Discord chatbot with commands to view current and upcoming stage rotations in Splatoon 3. You can also ask Splatbot for a random weapon suggestion, and more features are planned for the future as well.

## Usage
To add the official, hosted version of Splatbot, click the link in the project overview here on GitHub! Please be sure to allow Splatbot to send links and read message content, so that it can respond to your commands.

If you'd like to build and run your _own_ version of Splatbot, or to contribute new features, keep reading.

### Commands
Use `/help` or `Splatbot help` in chat to view a full list of commands.

#### How to read command arguments
Splatbot's commands follow a particular pattern. When a piece of information is required for the command, it will be marked with `<angle brackets>`. If it is an optional command argument, it will be marked with `[square brackets]`. Certain commands are more complex than others, so this rule can help you understand how to use them!

The default command to summon Splatbot is `splatbot`, `!sb`, or by using a Slash Command directly.

## Custom Splatbot Installation
To build and run Splatbot yourself, use the following steps. If you'd like to use a pre-assembled binary from the [Releases](https://github.com/hwdotexe/Splatbot/releases)
page, skip to **Step 4**.

#### Step 1
Clone the repository by using `git clone https://github.com/hwdotexe/Splatbot.git`

#### Step 2
Import Splatbot as a Maven project in your favorite IDE.

#### Step 3
Build Splatbot by using `mvn clean package assembly:single`

#### Step 4
Create a `.bat`/`.sh` file to launch the Splatbot binary `.jar`:
 
 ```shell script
java -jar name_of_splatbot_file.jar
```
 
After launching, the application will generate some first-time setup files.

#### Step 5
You'll need to create a new Discord application by visiting the [Discord Developer Portal](https://discord.com/developers/applications/). Choose
a name for your custom Splatbot, and follow Discord's online prompts. When finished, you can find the bot's Token (needed later) by clicking on
your app, going to "Bot" on the side, and clicking `Click to Reveal Token`.

#### Step 6 
Where you launched Splatbot, there is a new file in `/squids` called `giantSquid.json`. Open this file, and replace the value for `botAPIKey` with the token you created in the last step. You may notice that there is another option, `botAPIKeySecondary`. This is used for when you launch the bot in **debug mode**, in case you'd like to debug with a different Discord application. You can ignore this item for now.

#### Step 7
Launch the bot again, and it should finish the startup process and be ready to roll. You can now invite your bot to your Discord server
using the Discord Developer Portal - simply create an OAuth2 authorization URL for your bot, and use it to join the bot to a server
you have permission to change.

#### Step 8
Once the bot joins your server, it should send a welcome message if possible. Should this not happen, you can still attempt to summon the bot by using its name - for example, `splatbot help`. Have fun!
