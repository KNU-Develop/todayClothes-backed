import sys
import requests
from rembg import remove
from PIL import Image
from io import BytesIO
import os

if len(sys.argv) < 3:
    print("Usage: python remove_bg.py <input_image_url_or_path> <output_image_path>")
    sys.exit(1)

input_image_path_or_url = sys.argv[1]
output_image_path = sys.argv[2]

try:
    input_image = None
    output_image = None

    if input_image_path_or_url.startswith('http://') or input_image_path_or_url.startswith('https://'):
        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36"
        }
        response = requests.get(input_image_path_or_url, headers=headers)
        response.raise_for_status()
        input_image = Image.open(BytesIO(response.content))
    else:
        input_image = Image.open(input_image_path_or_url)

    output_image = remove(input_image)

    output_image.save(output_image_path)
    print(f"Background removed and saved to {output_image_path}")

except requests.exceptions.RequestException as e:
    print(f"Error downloading the image: {e}")
    sys.exit(1)

except Exception as e:
    print(f"Error processing the image: {e}")
    sys.exit(1)

finally:
    if input_image:
        input_image.close()
    if output_image:
        output_image.close()
