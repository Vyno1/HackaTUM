import argparse
import chat_gpt_communication as comm


def parse_args():
    parser = argparse.ArgumentParser(description="Chat GPT Parser")
    parser.add_argument("--file_path", required=True, help="Path of input file")
    parser.add_argument("--function_path", required=True, help="The function to write the tests for")
    parser.add_argument("--edge_cases", action="store_true", help="Flag set if edge case tests desired")
    parser.add_argument("--security", action="store_true", help="Flag set if security tests desired")
    parser.add_argument("--exception", action="store_true", help="Flag set if exception tests desired")
    parser.add_argument("--general_functionality", action="store_true",
                        help="Flag set if general functionality tests desired")
    parser.add_argument("--other", action="store_true", help="Flag set if other tests desired")
    parser.add_argument("--comment", default="", help="Optional comment if user wants other tests")
    parser.add_argument("--module", default="", help="What class/module we are in")

    args = parser.parse_args()

    params = {}

    file_path = args.file_path
    function_path = args.function_path
    comment = args.comment
    module = args.module

    params["edge_cases"] = args.edge_cases
    params["exception"] = args.exception
    params["security"] = args.security
    params["genral_functionality"] = args.general_functionality
    params["other"] = args.other

    return file_path, function_path, params, module, comment


def main():
    file_path, function_path, params, module, comment = parse_args()

    for key, value in params.items():
        if value:
            comm.communicate(key, file_path, comment, module, function_path)


if __name__ == "__main__":
    main()
