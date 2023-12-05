# HelmsmanProject
An interface with OpenAI's Assistant API with the simple goal of providing a shortcut to 
execute BASH scripts on the user's PC based on natural language input. A JavaFX GUI provides
a chatroom structure that interacts with a python server which manages API interaction.
The server retrieves generated BASH scripts from the thread, executes them on the user's
local machine, and then reports the results in the chatroom.

Instructions:
1) Make sure dependencies are installed and correctly configured: Java 17, JavaFX 17, Python
2) configure the config.json to have the correct information, ESPECIALLY the API key, which you can generate on OpenAI's website if you have a subscription
3) Run the server_body.py first, then GUI.java
