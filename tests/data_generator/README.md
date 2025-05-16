# Random Data Generation Program

This program allows generating random data based on a selected JSON template. It supports several JSON templates, including user data, statuses, and products with names, descriptions, and prices.

## Requirements

To run the program, you need to have the following libraries installed:

- [Faker](https://pypi.org/project/Faker/): A library for generating fake data such as names, emails, addresses, etc.

You can install this library using pip:

```bash
pip install faker
```

## Running the Program

1. Download or clone this project to your device.
2. Run the program:

```bash
python generate_data.py
```

The program will prompt you to choose one of the available templates and specify the number of records to generate.

## Available JSON Templates

1. **Template 1:**  
   Generates user data: `"name"` and `"email"`.
   - Example:  
     ```json
     {
       "name": "John Doe",
       "email": "john.doe@example.com"
     }
     ```  

2. **Template 2:**  
   Generates status data: `"status"`. The status is randomly selected from three values: `"active"`, `"inactive"`, `"pending"`.
   - Example:  
     ```json
     {
       "status": "active"
     }
     ```  

3. **Template 3:**  
   Generates product data: `"name"`, `"description"`, `"price"`. The price is randomly set between 10 and 500.
   - Example:  
     ```json
     {
       "name": "Example Product",
       "description": "This is an example product description.",
       "price": 99.99
     }
     ```  

## Example Execution

After running the program, choose one of the templates and specify the number of records to generate.

```plaintext
Select a JSON template to use:
1. Template 1: {"name": "name", "email": "email"}
2. Template 2: {"status": "status"}
3. Template 3: {"name": "name", "description": "description", "price": "price"}
Enter template number (1, 2, or 3): 3
Enter the number of records to generate: 3
```

**Output:**  

```json
[
  {
    "name": "TechCorp",
    "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
    "price": 279.88
  },
  {
    "name": "Innovative Solutions",
    "description": "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
    "price": 149.99
  },
  {
    "name": "Global Enterprises",
    "description": "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
    "price": 439.56
  }
]
```

## Customization

You can customize the program by modifying the range of generated values or adding new JSON templates. 
The program supports any modifications to templates that match keys
such as `"name"`, `"email"`, `"status"`, `"description"`, `"price"`, etc.

## Output

The generated data is saved to a JSON file with a name that includes a timestamp. 
This ensures that each file has a unique name and reflects the exact time the data was generated.
For example, a file might be named \`generated_data_20250129190044.json\`.