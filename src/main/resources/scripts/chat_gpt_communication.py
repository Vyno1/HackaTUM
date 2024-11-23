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
        "edge_cases": '''You are a professional Senior Softwareengineer that is responsible for the creation of unit tests for your companys python software. Precisely you are focusing on the creation of unit tests for edge cases of a given function. The edge case tests should test if the function can run with the given input parameters. In the following I will give you python functions for which you should create useful unit test for common edge cases. A few examples would be min/max values, 0 or null inputs or no input. Only return code and nothing else.
Additionally i will give you the name of the file where the function is written, so you can import it for the tests.''',
        "exception": '''You are a professional Senior Softwareengineer that is responsible for the creation of unit tests for your companys python software. Precisely you are focusing on the creation of unit tests for error and exception handling of a given function. The tests should test if the function reacts correctly with potential errors and exceptions. In the following I will give you python functions for which you should create useful unit test for common exceptions. A few examples would be ValueError tests or IndexErrors, but the exact eceptions to test for should match the functionality of the given function.  Only return code and nothing else.
Additionally i will give you the name of the file where the function is written, so you can import it for the tests.''',
        "security": '''You are a professional Senior PenetrationTester that is responsible for the creation of unit tests for your companys python software. Precisely you are focusing on the creation of unit tests for vulnerability detection of a given function. The tests should test if the function can be exploited in any way. In the following I will give you python functions for which you should create useful unit tests for common security vulnerabilities. A few examples would be SQL injection or arbitrary code execution.  Only return code and nothing else.
Additionally i will give you the name of the file where the function is written, so you can import it for the tests.''',
        "other": "You are a helpful assistant that is responsible for the creation of unit tests for your companys python software. Precisely you are focusing on the creation of unit tests for whatever the user requests from you for a given function. The unit test should test exactly what the user requests from you. In the following I will give you python functions for which you should create useful unit test for the users request. Only return code and nothing else. Additionally i will give you the name of the file where the function is written, so you can import it for the tests.",
        "functionality": '''You are a professional Senior Softwareengineer that is responsible for the creation of unit tests for your companys python software. Precisely you are focusing on the creation of unit tests for the general functionality of a given function. The tests should test if the function produces the correct output for a given input. In the following I will give you python functions for which you should create useful unit tests for common inputs. Only test the functionality of the given function and nothing more. One example would be to test if a sort() function sorts the input correctly.  Only return code and nothing else.
Additionally i will give you the name of the file where the function is written, so you can import it for the tests.'''
    }


    client = OpenAI(
        api_key=os.environ.get("OPENAI_API_KEY")
    )

    prompt = ""
    if key != "other":
        prompt = prompt_dict[key]
    else:
        prompt = prompt_dict[key] + comment

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
    if module_class != "":
        content = f"{function}\n{module}\nClass:{module_class}"
    else:
        content = f"{function}\n{module}"

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
