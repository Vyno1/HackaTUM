import argparse
import chat_gpt_communication as comm


def parse_args():
    parser = argparse.ArgumentParser(description="Chat GPT Parser")
    parser.add_argument("--file_path", required=True, help="Path of input file")
    parser.add_argument("--function_path", required=True, help="The function to write the tests for")
    parser.add_argument("--EDGE_CASES", action="store_true", help="Flag set if edge case tests desired")
    parser.add_argument("--SECURITY", action="store_true", help="Flag set if security tests desired")
    parser.add_argument("--EXCEPTION", action="store_true", help="Flag set if exception tests desired")
    parser.add_argument("--FUNCTIONALITY", action="store_true",
                        help="Flag set if general functionality tests desired")
    parser.add_argument("--ADDITIONAL_PROMPTS", default="", help="Optional commentS if user wants other tests")
    parser.add_argument("--module", default="", help="What class/module we are in")

    args = parser.parse_args()

    params = {}

    file_path = args.file_path
    function_path = args.function_path
    comment = args.ADDITIONAL_PROMPTS
    module = args.module

    params["edge_cases"] = args.EDGE_CASES
    params["exception"] = args.EXCEPTION
    params["security"] = args.SECURITY
    params["functionality"] = args.FUNCTIONALITY
    params["other"] = args.ADDITIONAL_PROMPTS

    return file_path, function_path, params, module, comment


def main():
    file_path, function_path, params, module, comment = parse_args()

    for key, value in params.items():
        if value:
            comm.communicate(key, file_path, comment, module, function_path)


if __name__ == "__main__":
    main()
