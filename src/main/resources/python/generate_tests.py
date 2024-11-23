from pathlib import Path
import os

out_dir = Path(__file__).parent.parent / "out"

if not os.path.isdir(out_dir):
    os.makedirs(out_dir)
out_file = out_dir / "out"

test_out = \
"""import os

def abc() -> int
    a = 1
    b = 3
    return a + b
"""

if __name__ == "__main__":
    with open(out_file, "w") as f:
        f.write(test_out)

    print("ello")
