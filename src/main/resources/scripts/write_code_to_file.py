import os


def write_code_to_file(path: str, content: str) -> None:
    directory = os.path.dirname(path)
    filename = os.path.basename(path).split(".")[0]

    file_path = os.path.join(directory, f"{filename}_test.py")

    if not os.path.exists(file_path):
        with open(file_path, 'x') as file:
            # Write some text to the file
            file.write(content)
    else:
        with open(file_path, 'w') as file:
            # Write some text to the file
            file.write(content)
