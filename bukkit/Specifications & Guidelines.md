## Specifications
#### Colors & Formatting:
* Colors and formatting normally go on until changed/removed, but they reset on new lines
* Color tags:
  * `[white]`, `[black]`, `[yellow]`, `[gold]`, `[aqua]`, `[dark_aqua]`, `[blue]`, `[dark_blue]`, `[light_purple]`, `[dark_purple]`, `[red]`, `[dark_red]`, `[green]`, `[dark_green]`, `[gray]`, `[dark_gray]`
  * When you use a color it will override the last one
* Formatting tags:
  * `[bold]`, `[italic]`, `[underlined]`, `[strikethrough]`, `[obfuscated]`
  * When you use a formatting tag it will enable this formatting
  * When you use a close tag of a format it will disable that formatting
* The color and formatting inside a hover is seperate (start with nothing, resets each line)
* Tags are not case sensitive
* The following tags are equivalents:
  * `[strikethrough]` = `[s]`
  * `[bold]` = `[b]`
  * `[italic]` = `[i]`
  * `[underline]` = `[u]`

#### Breaks (newlines):
* The break symbol is: `[break]`
* Using it will cause the text to wrap to the next line in chat, so if `[break][break]` is used it will leave an empty line
* The end of a list element (string) does NOT automatically result in a break in the chat
* After using `[break]` there may not be more text in this line, from there only more `[break]` tags are allowed (more text can go in a new list entry)

#### Special effect tags:
* Tags are for adding special effects to a part of the text, used as the following: `  link: http://google.com`
* Supported tags:
  * Link: If clicked it will cause a “Do you want to visit this website?” popup
  * Hover: If hovered over with the mouse it will give information in a little box
    * You can use multiple lines with this tag to get multi-line hover popups
  * Suggest: If clicked it will put a string into the chat box
  * Command: If clicked it will execute a command as the player
* The hover tag supports all colors and formatting like with normal text
* Tags do not need to be indented, but normally are for readability
* Tags are not case sensitive

#### Escaping:
* `[esc]` can be used to escape 1 character, so if you want to have `link: example.com` as text you need a `[esc]` in front of this. Then the first character will be escaped and since `ink: ` is not a valid tag anymore it will be shown as text instead of being used as link on the previous text part.
* Placing `[esc][esc]` in the text will result in the text `[esc]` being printed, the first escape will be handled as such, this will escape `[` and therefore the text behind this will be `esc]` and this is just text
* Also works for variables

## Guidelines
* Use lowercase characters for tags
* When using a special effect tag indent the line by 4 spaces (up for debate)

## Examples
The below examples assume writing the format in YAML format.

#### Simple messages
A simple message without any formatting.
```yaml
message: "You do not have permission to use that command!"
```
![](https://cloud.githubusercontent.com/assets/6951068/15986885/2dbd4016-3015-11e6-9525-0cbea6155a78.png)


A message that is bold and red to make it look more serious.
```yaml
message: "[red][bold]You really do not have permission!"
```
![](https://cloud.githubusercontent.com/assets/6951068/15986886/2e8b1310-3015-11e6-853b-3b497a304c6d.png)


A message with more color and format changes.
```yaml
message: "[red][bold]You[/bold] really do not have [green][underline]permission[reset]!"
```
![](https://cloud.githubusercontent.com/assets/6951068/15986887/2f399ad4-3015-11e6-945d-94db3aa52579.png)


A multi-line message.
```yaml
message:
  - "First line"
  - "[red], still on the first line[break]"
  - "Second line because of the break above"
  - ", but no color."
```
![](https://cloud.githubusercontent.com/assets/6951068/15987126/9c46c8b0-301d-11e6-8ca8-3bae662d0a0e.png)


#### Using hover/click parts

A message with some hover tooltips, each hover will start on a new line.
```yaml
message:
  - "Hello there!"
  - "    hover: Have a good day!"
  - "    hover: Second tooltip line :)"
```
![](https://cloud.githubusercontent.com/assets/6951068/15987136/170a00f8-301e-11e6-8e46-84c5c21d5204.png)


A message with a tooltip and command.
```yaml
message:
  - "Hello there!"
  - "    hover: Click me to use /help!"
  - "    command: /help"
```
![](https://cloud.githubusercontent.com/assets/6951068/15987147/68c905a6-301e-11e6-8cd2-e3c7ca329855.png)






