from pathlib import Path

out_file = Path(__file__).parent.parent / "out" / "out"

test_out = \
"""import os

def abc() -> int
    a = 1
    b = 3
    return a + b
"""

with open(out_file, "w") as f:
    f.write(test_out)

