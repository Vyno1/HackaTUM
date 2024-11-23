import json
from operator import index
import os

from openai import OpenAI
from write_code_to_file import write_code_to_file


def communicate(key: str, file_path_py: str, comment: str, module_class: str, file_path_code: str) -> None:
    """
    Args:
        key (str): The prompt or keyword to use (e.g., security).
        file_path_py (str): The path to the file.
        file_path_code (str): Path to the copied code in a file.
        comment (str, optional): An additional comment, if any. Ignored if empty.
        module_class (str): The module that needs to be specified to ChatGPT.
    """

    prompt_dict = {
        "edge_cases": "prompts/EdgeCasesTests.txt",
        "exception": "prompts/ExceptionTests.txt",
        "security": "prompts/SecurityTests.txt",
        "other": "",
        "genral_functionality": "prompts/GeneralTests.txt"
    }

    with open('OpenAI_key.txt', 'r', encoding="utf-8-sig") as file:
        # Read the first line from the file
        open_ai_key = str(file.readline().strip())

    client = OpenAI(
        api_key=open_ai_key
    )
    prompt = ""
    if key != "other":
        path = prompt_dict[key]
        with open(path, 'r', encoding='utf-8') as file:
            file_content = file.read()

        prompt = file_content
    else:
        prompt = comment

    assistant = client.beta.assistants.create(
        name="Testcreator",
        instructions=prompt,
        tools=[],
        model="gpt-4o",
    )

    thread = client.beta.threads.create()

    function = ""
    with open(file_path_code, 'r', encoding='utf-8') as file:
        function = file.read()

    module = os.path.basename(file_path_py).split(".")[0]
    content = f"{function}\n{module}\n{module_class}"

    message = client.beta.threads.messages.create(
        thread_id=thread.id,
        role="user",
        content=content
    )

    run = client.beta.threads.runs.create_and_poll(
        thread_id=thread.id,
        assistant_id=assistant.id,
        instructions=""
    )

    if run.status == 'completed':
        messages = client.beta.threads.messages.list(
            thread_id=thread.id
        )
        output = json.loads(messages.model_dump_json())["data"][0]["content"][0]["text"]["value"]
        write_code_to_file(file_path_py, clean_output(output), key)
    else:
        print(run.status)


def clean_output(output: str) -> str:
    return output.replace("```python", "").replace("```", "")
