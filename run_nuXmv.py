import subprocess

def run_nuxmv(model_filename):
    # Run the command
    nuxmv_process = subprocess.Popen(["nuXmv", model_filename], stdin=subprocess.PIPE, stdout=subprocess.PIPE, universal_newlines=True)
    output_filename = model_filename.split(".")[0] + ".out"

    stdout, _ = nuxmv_process.communicate()

    # Save output to file
    with open(output_filename, "w") as f:
        f.write(stdout)
    print(f"Output saved to {output_filename}")

    return output_filename