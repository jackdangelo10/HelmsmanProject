import socket
import threading
import json
import os
import api_handler
import executor

# facilitates communication between the server and the GPT-4 API     

def handle_client(connection):
    # prompt to lead every message sent to the GPT-4 API
    # NOTE: minimize prompt length to reduce token usage
    message_prompt = """ 
    The only output should be BASH code.
    Do this even if the user input is conversational. Use BASH code to echo a response.
    The BASH scripts should start with #!/bin/env bash
    The BASH scripts should report the result of the excution of the script in natural language via echo statements.
    """
    
    # loop to receive user input and send it to the GPT-4 API
    user_input = ""
    while True:
        try:    
            # Receiving data from Java client
            user_input = connection.recv(1024).decode('utf-8')
            if not user_input:
                break
            # server-side logging
            print(f"Received data from client: {user_input}")
        except ConnectionResetError as e:
            print(f"Connection was reset: {e}")
            break
        except Exception as e:
            print(f"An error occurred: {e}")
            break
        
        
        script = api_handler.send_to_helmsman(assistant, client, thread, message_prompt + user_input)
        # server-side logging
        print("BASH script: " + script)
        
        
        stdout, stderr = executor.execute_bash_script(script)
        # server-side logging
        print("This is the BASH script output generated: " + stdout)    
        
        # error checking
        if stdout == "":
            print("Error: No output was generated")


        # Sending response back to Java client
        # prevents blocking waiting for newline
        response = response + '\n'
        connection.sendall(response.encode('utf-8'))
        # server-side logging
        print("Sent response to client")

    connection.close()
    

        
def start_server():
    global assistant, client, thread

    # If the attempt to connect to the json stored gpt fails, instantiate a new gpt-4 assistant
    assistant, client, thread = api_handler.connect_helmsman_gpt()
    if assistant is None or client is None or thread is None:
        assistant, client, thread = api_handler.initialize_helmsman_gpt()
    else:
        print("Connected to the GPT thread")
    #    
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(('localhost', 65432))
    server.listen()

    # server-side logging
    print("Server is listening on localhost:65432")

    # continuously listen for incoming connections
    while True:
        conn, addr = server.accept()
        print(f"Connected by {addr}")
        client_thread = threading.Thread(target=handle_client, args=(conn,))
        client_thread.start()
        
        
if __name__ == "__main__":
    start_server()
