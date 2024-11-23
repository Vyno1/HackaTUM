﻿import json
from operator import index

from openai import OpenAI


def communicate():
    client = OpenAI(
        api_key="",
        # This is the default and can be omitted
    )

    assistant = client.beta.assistants.create(
        name="Test",
        instructions='''You are a professional Senior Softwareengineer that is responsible for the creation of unit tests for your companys python software. Precisely you are focusing on the creation of unit tests for the general functionality of a given function. The tests should test if the function produces the correct output for a given input. In the following I will give you python functions for which you should create useful unit tests for common inputs. Only test the functionality of the given function and nothing more. One example would be to test if a sort() function sorts the input correctly.
                        Additionally i will give you the name of the file where the function is written, so you can import it for the tests. Only return code.''',
        tools=[],
        model="gpt-4o",
    )

    thread = client.beta.threads.create()

    while True:
        #content = input("Say smth: ")
        message = client.beta.threads.messages.create(
            thread_id=thread.id,
            role="user",
            content='''def merge(arr, l, m, r):
    n1 = m - l + 1
    n2 = r - m

    # create temp arrays
    L = [0] * (n1)
    R = [0] * (n2)

    # Copy data to temp arrays L[] and R[]
    for i in range(0, n1):
        L[i] = arr[l + i]

    for j in range(0, n2):
        R[j] = arr[m + 1 + j]

    # Merge the temp arrays back into arr[l..r]
    i = 0  # Initial index of first subarray
    j = 0  # Initial index of second subarray
    k = l  # Initial index of merged subarray

    while i < n1 and j < n2:
        if L[i] <= R[j]:
            arr[k] = L[i]
            i += 1
        else:
            arr[k] = R[j]
            j += 1
        k += 1

    # Copy the remaining elements of L[], if there
    # are any
    while i < n1:
        arr[k] = L[i]
        i += 1
        k += 1

    # Copy the remaining elements of R[], if there
    # are any
    while j < n2:
        arr[k] = R[j]
        j += 1
        k += 1
        
        
        filename: test.py'''
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
            print(json.loads(messages.model_dump_json())["data"][0]["content"][0]["text"]["value"])
        else:
            print(run.status)


if __name__ == '__main__':
    communicate()