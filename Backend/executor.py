import subprocess

def execute_bash_script(script):
    # Write the script to a file
    script_filename = "script.sh"
    with open(script_filename, "w") as f:
        f.write(script)
    
    # Execute the script and capture its output
    result = subprocess.run(["bash", script_filename], capture_output=True, text=True)
    # server-side logging
    print("This was the output generated after the script was run: " + result.stdout)
    
    # Return the stdout and stderr as a tuple
    return result.stdout, result.stderr