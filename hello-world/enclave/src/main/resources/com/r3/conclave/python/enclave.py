from torchvision import models
import torch
import os
import os.path

print("I am in " + os.getcwd())

class EnclaveMail:
    def __init__(self, body, authenticated_sender, envelope):
        self.body = body
        self.authenticated_sender = authenticated_sender
        self.envelope = envelope

def enclave_sign(data):
    # __enclave global variable is set by PythonEnclaveAdapter
    return __enclave.pythonSign(data)

# TODO enclaveInstanceInfo API attribute

# Hacky way to convert byte arrays to python 'bytes':
# https://groups.google.com/g/jep-project/c/dIWcEQL7-UY/m/bBEdjHysAQAJ
def __convert_jbytes(jbytes):
    if jbytes is not None:
        return bytes(b % 256 for b in jbytes)


body = None
def receive_enclave_mail(mail):

    # Load the model from a file
    alexnet = torch.load("alexnet-pretrained.pt")

    # Prepare a transform to get the input image into a format (e.g., x,y dimensions) the classifier
    # expects.
    from torchvision import transforms
    transform = transforms.Compose([
        transforms.Resize(256),
        transforms.CenterCrop(224),
        transforms.ToTensor(),
        transforms.Normalize(
        mean=[0.485, 0.456, 0.406],
        std=[0.229, 0.224, 0.225]
    )])

    # Load the image.
    from PIL import Image
    img = Image.open("input.jpg")

    # Apply the transform to the image.
    img_t = transform(img)

    # Magic (not sure what this does).
    batch_t = torch.unsqueeze(img_t, 0)

    # Prepare the model and run the classifier.
    alexnet.eval()
    out = alexnet(batch_t)

    # Load the classes from disk.
    with open('classes.txt') as f:
        classes = [line.strip() for line in f.readlines()]

    # Sort the predictions.
    _, indices = torch.sort(out, descending=True)

    # Convert into percentages.
    percentage = torch.nn.functional.softmax(out, dim=1)[0] * 100

    # Print the 5 most likely predictions.
    with open("result.txt", "w") as outfile:
        outfile.write(str([(classes[idx], percentage[idx].item()) for idx in indices[0][:5]]))

    print("Done. The result was written to `result.txt`.")